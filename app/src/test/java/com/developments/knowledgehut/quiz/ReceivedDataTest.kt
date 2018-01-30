package com.developments.knowledgehut.quiz


import org.apache.commons.text.StringEscapeUtils
import org.junit.Assert
import org.junit.Test

class ReceivedDataTest {

    @Test
    fun decodeHTML() {
        val encodedHTMLString = "Don&#039;t forget that &pi; = 3.14 &amp; doesn&#039;t equal 3."
        val decodedHTMLString = StringEscapeUtils.unescapeHtml4(encodedHTMLString)

        Assert.assertEquals( "Don't forget that π = 3.14 & doesn't equal 3.", decodedHTMLString)
    }
}
