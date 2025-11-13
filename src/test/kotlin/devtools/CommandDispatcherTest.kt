package devtools

import context.ServerContext
import devtools.cmd.Command
import devtools.cmd.CommandDispatcher
import devtools.cmd.CommandRequest
import devtools.cmd.CommandResult
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import org.junit.jupiter.api.assertThrows
import utils.JSON
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Command dispatcher test
 */
class CommandDispatcherTest {
    private val context = ServerContext.fake()

    @BeforeTest
    fun setupJson() {
        JSON.initialize(Json {
            ignoreUnknownKeys = true
            isLenient = true
        })
    }

    @Test
    fun `CommandDispatcher execute failed when command is not registered`() = runTest {
        val dispatcher = CommandDispatcher(context)

        val obj = buildJsonObject {
            put("field1", JsonPrimitive("pid123"))
            put("field2", JsonPrimitive(1))
        }

        val input = CommandRequest("Example", obj)

        assertTrue(dispatcher.handleCommand(input) is CommandResult.CommandNotFound)
    }

    @Test
    fun `CommandDispatcher register throws on duplicate command`() = runTest {
        val dispatcher = CommandDispatcher(context)
        dispatcher.register(ExampleCommand())

        assertThrows<IllegalArgumentException> {
            dispatcher.register(ExampleCommand())
        }
    }

    @Test
    fun `CommandDispatcher successfully execute perfect command`() = runTest {
        val dispatcher = CommandDispatcher(context)
        dispatcher.register(ExampleCommand())

        val obj = buildJsonObject {
            put("field1", JsonPrimitive("pid123"))
            put("field2", JsonPrimitive(12))
            put("field3", JsonPrimitive(true))
        }

        val input = CommandRequest("Example", obj)

        assertEquals(CommandResult.Executed, dispatcher.handleCommand(input))
    }

    @Test
    fun `CommandDispatcher successfully execute command with unprovided optional`() = runTest {
        val dispatcher = CommandDispatcher(context)
        dispatcher.register(ExampleCommand())

        val obj = buildJsonObject {
            put("field1", JsonPrimitive("pid123"))
            put("field2", JsonPrimitive(12))
        }

        val input = CommandRequest("Example", obj)

        assertEquals(CommandResult.Executed, dispatcher.handleCommand(input))
    }

    @Test
    fun `CommandDispatcher execute failed with unprovided required value`() = runTest {
        val dispatcher = CommandDispatcher(context)
        dispatcher.register(ExampleCommand())

        val obj = buildJsonObject {
            put("field2", JsonPrimitive(12))
        }

        val input = CommandRequest("Example", obj)

        assertTrue(dispatcher.handleCommand(input) is CommandResult.SerializationFails)
    }

    @Test
    fun `CommandDispatcher execute success on string argument type mismatch`() = runTest {
        val dispatcher = CommandDispatcher(context)
        dispatcher.register(ExampleCommand())

        val obj = buildJsonObject {
            put("field1", JsonPrimitive(12))
            put("field2", JsonPrimitive(12))
        }

        val input = CommandRequest("Example", obj)

        assertEquals(CommandResult.Executed, dispatcher.handleCommand(input))
    }

    @Test
    fun `CommandDispatcher execute failed on non-string argument type mismatch`() = runTest {
        val dispatcher = CommandDispatcher(context)
        dispatcher.register(ExampleCommand())

        val obj = buildJsonObject {
            put("field1", JsonPrimitive("pid123"))
            put("field2", JsonPrimitive("dsafdsf"))
        }

        val input = CommandRequest("Example", obj)

        assertTrue(dispatcher.handleCommand(input) is CommandResult.SerializationFails)
    }

    @OptIn(ExperimentalSerializationApi::class)
    @Test
    fun `CommandDispatcher execute failed on null value for non-null field`() = runTest {
        val dispatcher = CommandDispatcher(context)
        dispatcher.register(ExampleCommand())

        val obj = buildJsonObject {
            put("field1", JsonPrimitive(null))
            put("field2", JsonPrimitive(123))
        }

        val input = CommandRequest("Example", obj)

        assertTrue(dispatcher.handleCommand(input) is CommandResult.SerializationFails)
    }

    @Test
    fun `CommandDispatcher execute failed when command execution throws`() = runTest {
        val dispatcher = CommandDispatcher(context)
        dispatcher.register(ExampleCommand())

        val obj = buildJsonObject {
            put("field1", JsonPrimitive("pid123"))
            put("field2", JsonPrimitive(1))
        }

        val input = CommandRequest("Example", obj)

        assertTrue(dispatcher.handleCommand(input) is CommandResult.Error)
    }

    @Test
    fun `CommandDispatcher execute failed when command has internal logic or domain error`() = runTest {
        val dispatcher = CommandDispatcher(context)
        dispatcher.register(ExampleCommand())

        val obj = buildJsonObject {
            put("field1", JsonPrimitive("pid123"))
            put("field2", JsonPrimitive(1002))
        }

        val input = CommandRequest("Example", obj)

        assertTrue(dispatcher.handleCommand(input) is CommandResult.ExecutionFailure)
    }
}

@Serializable
data class ExampleArgument(
    val field1: String,
    val field2: Int,
    val field3: Boolean = false,
)

class ExampleCommand : Command<ExampleArgument> {
    override val name: String = "Example"
    override val shortDescription: String = "This is an example command"
    override val detailedDescription: String = """
This is an example command.

This command exists purely for demonstration and testing purposes. It also serves as a reference for how to properly implement a command. This message is also used as `detailedDescription` in the code.

Arguments:

- field1: String - Defines the first field used to control behavior X.
- field2: Int - Represents parameter Y for demonstration.
- field3: Boolean - (optional) Determines whether to enable feature Z.
    """.trimIndent()
    override val completionMessage: String = "Item {} successfully given to {}"
    override val serializer: KSerializer<ExampleArgument> = ExampleArgument.serializer()

    override suspend fun execute(serverContext: ServerContext, arg: ExampleArgument): CommandResult {
        if (arg.field2 == 1) {
            throw Exception()
        }
        if (arg.field2 > 1000) {
            return CommandResult.ExecutionFailure("field2 greater than 1000")
        }

        println("Executed success with arg=$arg")
        return CommandResult.Executed
    }
}
