import java.io.InputStream
import java.io.InputStreamReader
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class Config(fileName: String) {
    private val data: Map<String, String> = extractContent(fileName)

    companion object {
        private fun extractContent(fileName: String): Map<String, String> {
            val inputStream = getResource(fileName) ?: throw IllegalArgumentException()
            val data = mutableMapOf<String, String>()
            InputStreamReader(inputStream).use { reader ->
                reader.forEachLine { line ->
                    val parts = line.split("=").map { it.trim() }
                    require(parts.size == 2)
                    data[parts[0]] = parts[1]
                }
            }
            return data
        }
    }

    operator fun provideDelegate(thisRef: Any?, property: KProperty<*>): ReadOnlyProperty<Any?, String> {
        require(property.name in data)
        return ReadOnlyProperty { thisRef, property -> data[property.name] ?: throw IllegalArgumentException() }
    }
}

@Suppress(
    "RedundantNullableReturnType",
    "UNUSED_PARAMETER",
)
fun getResource(fileName: String): InputStream? {
    // do not touch this function
    val content =
        """
        |valueKey = 10
        |otherValueKey = stringValue 
        """.trimMargin()

    return content.byteInputStream()
}
