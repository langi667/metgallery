package de.stefanlang

import de.stefanlang.core.utils.Empty
import org.junit.Assert.*
import org.junit.Test

class EmptyUnitTest {
    @Test
    fun allNull() {
        assertTrue(Empty.allNull(null, null, null))
        assertFalse(Empty.allNull("", null, null))
    }

    @Test
    fun allNullOrBlank() {
        assertTrue(Empty.allNullOrBlank(null, null, null))
        assertTrue(Empty.allNullOrBlank("", null, null))
        assertTrue(Empty.allNullOrBlank("", " ", null))
        assertFalse(Empty.allNullOrBlank("", "not empty", null))
    }
}