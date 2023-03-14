package de.stefanlang
import de.stefanlang.core.models.HyperLink
import de.stefanlang.core.utils.Empty
import org.junit.Assert.*
import org.junit.Test

class HyperLinkUnitTest {
    @Test
    fun createList() {
        assertTrue(HyperLink.createList("", " ", "   ").isEmpty())
        assertTrue(HyperLink.createList("", " ", "   ", "https://www.google.de").size == 1)
    }
}