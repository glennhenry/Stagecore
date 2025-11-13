package devtools.cmd

/**
 * Represents the outcome of executing a server command.
 */
sealed class CommandResult(val message: String = "") {
    /**
     * The command executed successfully.
     */
    object Executed : CommandResult() {
        override fun toString(): String = "Executed"
    }

    /**
     * The command failed to execute due to serialization failure.
     *
     * This includes incorrect types, insufficient arguments, or any illegal input in the command.
     */
    class SerializationFails(message: String) : CommandResult(message) {
        override fun toString(): String = "SerializationFails: $message"
    }

    /**
     * The command failed to execute because it was not found (not registered).
     */
    class CommandNotFound(message: String) : CommandResult(message) {
        override fun toString(): String = "CommandNotFound: $message"
    }

    /**
     * Command failed during execution due to a logical or domain error (e.g., player not found).
     */
    class ExecutionFailure(message: String) : CommandResult(message) {
        override fun toString(): String = "ExecutionFailure: $message"
    }

    /**
     * An unexpected error occurred during command execution.
     */
    class Error(message: String) : CommandResult(message) {
        override fun toString(): String = "Error: $message"
    }
}
