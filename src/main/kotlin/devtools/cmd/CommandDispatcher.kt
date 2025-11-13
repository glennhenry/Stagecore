package devtools.cmd

import context.ServerContext
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.JsonObject
import utils.JSON
import utils.logging.Logger

/**
 * Dispatch and execute server registered commands.
 *
 * Server commands offers the ability to control and monitor the server.
 * It enables user to modify server's behavior, such as modifying player's data.
 *
 * The server accepts command from the API server, which is typically
 * operated from the external web devtools.
 *
 * How to use:
 * - Implement [Command].
 * - Register the command with [register].
 * - Via the devtools, select command to be executed and input arguments.
 *
 * See example in `test.devtools.CommandDispatcherTest`.
 */
class CommandDispatcher(private val serverContext: ServerContext) {
    private val commands = mutableMapOf<String, Command<*>>()

    /**
     * Register a server command.
     *
     * @param command The command to be registered.
     * @param T The typed argument for the command.
     *
     * @throws IllegalArgumentException If the same command name has already been registered,
     *                                  or when command implementation fail to provide correct `argInfo`.
     */
    fun <T> register(command: Command<T>) {
        when {
            commands.containsKey(command.name) -> {
                throw IllegalArgumentException(
                    "Duplicate command registration for '${command.name}'; Each command's name must be unique."
                )
            }
        }

        commands[command.name] = command
    }

    /**
     * Handle command request.
     *
     * It involves doing a lookup to the registered commands, deserializing raw
     * arguments input in JSON to the associated typed command argument, then
     * calling `execute` method in the command implementation.
     *
     * @return [CommandResult] that represents the outcome.
     */
    @Suppress("UNCHECKED_CAST")
    suspend fun handleCommand(request: CommandRequest): CommandResult {
        val cmd = (commands[request.name]
            ?: return CommandResult.CommandNotFound("Failed to execute command '${request.name}': Command is unknown."))
                as Command<Any?>

        try {
            val argsJson = JsonObject(request.args)
            val argsObj = JSON.json.decodeFromJsonElement(cmd.serializer, argsJson)
            Logger.info { "Received command '${cmd.name}' with args=$argsObj" }

            val output = cmd.execute(serverContext, argsObj)
            Logger.info { "Done executing command '${cmd.name}'; result='$output'" }
            return output
        } catch (e: SerializationException) {
            val msg = "Failed to deserialize arguments for command '${cmd.name}'. Ensure the provided argument matches the expected argument structure; error: ${e.message ?: e}"
            Logger.error { msg }
            return CommandResult.SerializationFails(msg)
        } catch (e: IllegalArgumentException) {
            val msg = "Invalid argument for command '${cmd.name}', illegal argument provided; error: ${e.message ?: e}"
            Logger.error { msg }
            return CommandResult.SerializationFails(msg)
        } catch (e: Exception) {
            val msg = "Error thrown while executing the command '${cmd.name}'; error: ${e.message ?: e}"
            Logger.error { msg }
            return CommandResult.Error(msg)
        }
    }
}
