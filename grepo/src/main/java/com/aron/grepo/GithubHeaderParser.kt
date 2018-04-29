package com.aron.grepo

import com.aron.grepo.models.GithubUrl
import com.aron.grepo.models.UrlRel
import okhttp3.Headers
import java.net.URL
import java.util.regex.Pattern

/**
 * @author Georgel Aron
 * @since 29/04/2018
 * @version 1.0.0
 */
object GithubHeaderParser {

    /**
     * Parse the headers and retrieve the navigation links
     */
    fun getNavigationLinks(headers: Headers): List<GithubUrl> {
        val list = ArrayList<GithubUrl>()

        headers.get("link")?.let { links ->
            links.split(",").forEach {
                val githubUrl = parseNavigationLink(it)
                if (githubUrl != null) {
                    list.add(githubUrl)
                }
            }
        }

        return list
    }

    private fun parseNavigationLink(value: String): GithubUrl? {
        val re1 = ".*?"
        // github url
        val re2 = "((?:http|https)(?::\\/{2}[\\w]+)(?:[\\/|\\.]?)(?:[^\\s\"]*))"
        val re3 = ".*?"
        // 'rel' word; not interested
        val re4 = "(?:[a-z][a-z]+)"
        val re5 = ".*?"
        // rel value
        val re6 = "((?:[a-z][a-z]+))"

        val p = Pattern.compile(re1 + re2 + re3 + re4 + re5 + re6, Pattern.CASE_INSENSITIVE)
        val m = p.matcher(value)
        if (m.find()) {
            val url = m.group(1)
            val rel = m.group(2)

            return GithubUrl(url.take(url.length - 2), UrlRel.fromString(rel))
        }
        return null
    }
}