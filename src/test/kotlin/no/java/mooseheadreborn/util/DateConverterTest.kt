package no.java.mooseheadreborn.util

import org.assertj.core.api.Assertions
import org.junit.Test

class DateConverterTest {
    @Test
    fun shouldConvertDate() {
        val offset = DateConverter.toOffset("202408011755")
        Assertions.assertThat(offset).isNotNull()
    }
}