package devtools.cmd

import utils.JSON
import kotlin.reflect.full.memberProperties

/**
 * Serves as a common base class for command implementations.
 *
 * This base class verifies (through [validate]) that each command:
 * - Has [ArgumentInfo] entry for each field in the command’s argument type [T].
 * - Does not provide extraneous argument information.
 * - Optional arguments declare a valid default value.
 * - Default value matches actual Kotlin default.
 *
 * @param T The argument type accepted by this command.
 *
 * @throws IllegalArgumentException if any of the four verification goes wrong.
 */
abstract class BaseCommand<T> : Command<T> {
    override fun validate() {
        val serialDesc = serializer.descriptor
        val fieldNames = (0 until serialDesc.elementsCount).map { serialDesc.getElementName(it) }.toSet()

        for (name in fieldNames) {
            // Ensure each field's info are provided
            val info = argInfo[name]
                ?: throw IllegalArgumentException(
                    "Command '$name' is missing ArgumentInfo for field '$name'"
                )

            // Ensure optional field specify a default value
            if (!info.required && info.defaultValue == null)
                throw IllegalArgumentException(
                    "Optional argument '$name' in command '${this.name}' must specify a default value."
                )

            // Ensure the default value matches the real Kotlin default
            val defaultInstance = try {
                serializer.deserialize(JSON.json.decodeFromString("{}"))
            } catch (e: Exception) {
                null
            }

            if (defaultInstance != null && info.defaultValue != null) {
                val actualVal = defaultInstance::class.memberProperties
                    .firstOrNull { it.name == name }
                    ?.getter
                    ?.call(defaultInstance)
                    ?.toString()

                if (actualVal != null && actualVal != info.defaultValue) {
                    throw IllegalArgumentException(
                        "Default value mismatch for '$name' in command '${this.name}'; argInfo='${info.defaultValue}' actual='$actualVal'"
                    )
                }
            }
        }

        // Ensure argInfo does not have extra entries
        for (extra in argInfo.keys - fieldNames) {
            throw IllegalArgumentException(
                "Command '${this.name}' has extraneous ArgumentInfo for unknown field '$extra'"
            )
        }
    }
}
