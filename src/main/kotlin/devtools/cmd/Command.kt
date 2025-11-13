package devtools.cmd

import context.ServerContext
import kotlinx.serialization.KSerializer

/**
 * Represents a server command that can be invoked to perform a specific action in server.
 *
 * See `test.devtools.CommandDispatcherTest` for example.
 *
 * @property name A human-readable name for the command. Must be unique to other commands.
 * @property shortDescription A brief explanation of what the command does.
 * @property detailedDescription A detailed explanation of the command including
 *                               argument type and purpose details.
 * @property completionMessage A message displayed after the command completes successfully.
 * @property serializer The serializer that defines how to encode or decode the argument type [T].
 * @param T The data class type defining the structure of the command’s arguments.
 */
interface Command<T> {
    val name: String
    val shortDescription: String
    val detailedDescription: String
    val completionMessage: String
    val serializer: KSerializer<T>

    /**
     * Execution logic of the command.
     *
     * @param serverContext The server's state.
     * @param arg The fully deserialized and validated argument object.
     *
     * @return Result of command execution, which should be any of the three:
     * - [CommandResult.Executed]
     * - [CommandResult.ExecutionFailure]
     * - [CommandResult.Error]
     */
    suspend fun execute(serverContext: ServerContext, arg: T): CommandResult
}
