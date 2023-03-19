package de.stefanlang

import de.stefanlang.metgallerybrowser.utils.JSONParser
import org.junit.Assert.assertEquals
import org.junit.Test

class JSONParserUnitTest {
    data class TestClass(
        var id: Int = 0,
        var height: Float = .0f,
        var length: Long = 0,
        var title: String = "",
        var siblings: List<Int> = emptyList(),
        var children: List<TestClass> = emptyList()
    )

    @Test
    fun test_mapObjectFrom() {
        val jsonStr = "{\n" +
                "  \"id\": 1,\n" +
                "  \"height\": 1003.3456,\n" +
                "  \"title\": \"Test\",\n" +
                "  \"siblings\": [\n" +
                "    1,\n" +
                "    2,\n" +
                "    3,\n" +
                "    4,\n" +
                "    5\n" +
                "  ],\n" +
                "  \"children\": [\n" +
                "    {\n" +
                "      \"id\": 2,\n" +
                "      \"height\": 10.667,\n" +
                "      \"title\": \"Test Sibling\",\n" +
                "      \"siblings\": [\n" +
                "        5,\n" +
                "        6,\n" +
                "        7,\n" +
                "        8\n" +
                "      ]\n" +
                "    }\n" +
                "  ]\n" +
                "}"

        val result = JSONParser.mapObjectFrom<TestClass>(jsonStr.encodeToByteArray())

        assertEquals(result.id, 1)
        assertEquals(result.height, 1003.3456f)
        assertEquals(result.title, "Test")

        assertEquals(result.siblings, listOf(1, 2, 3, 4, 5))
        assertEquals(
            result.children, listOf(
                TestClass(
                    id = 2,
                    height = 10.667f,
                    title = "Test Sibling",
                    siblings = listOf(5, 6, 7, 8)
                )
            )
        )
    }
}