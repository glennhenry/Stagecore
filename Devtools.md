# Devtools

TBA, not yet made.

## Commands

### Implementation

All command implementations are located under the package `src/main/kotlin/devtools/cmd`.

In short, dev can create command by implementing the `Command<T>` interface with a typed input argument `T`, and define the execution logic inside the `execute` method. Command implementation should have a clear short and detailed description, which will be displayed in the web UI. Last but not least, they should register the command implementation with `CommandDispatcher.register` from `ServerContext`.

Please consult the code directly for implementation details.

### List of Available Commands

The short and detailed description of commands implementation are also written here for quick information lookup.

#### Example

This is an example command.

This command exists purely for demonstration and testing purposes. It also serves as a reference for how to properly implement a command. This message is also used as `detailedDescription` in the code.

Arguments:

- field1: String - Defines the first field used to control behavior X.
- field2: Int - Represents parameter Y for demonstration.
- field3: Boolean - (optional) Determines whether to enable feature Z.
