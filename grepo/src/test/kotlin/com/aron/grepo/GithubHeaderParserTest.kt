package com.aron.grepo

import okhttp3.Headers
import org.junit.Test

/**
 * @author Georgel Aron
 * @since 03/05/2018
 * @version 1.0.0
 */
class GithubHeaderParserTest {

    @Test
    fun whenParsingLinkHeaderShouldSucceed() {
        val headers: Headers = Headers.of("link", "<https://api.github.com/user/66577/repos?per_page=15&page=2&sort=created>; rel=\"next\", <https://api.github.com/user/66577/repos?per_page=15&page=7&sort=created>; rel=\"last\"")

        val list = GithubHeaderParser.getNavigationLinks(headers)
        assert(list.isNotEmpty())
    }

    @Test
    fun whenParsingLinkHeaderShouldFail() {
        val headers: Headers = Headers.of("link", "")

        val list = GithubHeaderParser.getNavigationLinks(headers)
        assert(list.isEmpty())
    }
}