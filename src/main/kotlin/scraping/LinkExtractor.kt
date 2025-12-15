package org.example.scraping

import it.skrape.core.htmlDocument
import it.skrape.fetcher.HttpFetcher
import it.skrape.fetcher.skrape
import it.skrape.fetcher.response
import it.skrape.selects.eachHref
import it.skrape.selects.html5.p
import org.example.DirectedWeightedGraph


class LinkExtractor {
    val graph = DirectedWeightedGraph<String>()
    fun extract(articleName: String, depth: Int): DirectedWeightedGraph<String> {
        val extracted = skrape(HttpFetcher) {
            request {
                url = "https://en.wikipedia.org/wiki/$articleName"
            }
            response {
                htmlDocument {
                    ".mw-content-ltr" {
                        findFirst {
                            findAll {
                                eachHref
                            }
                        }
                    }
                }
            }
        }

        var filtered = extracted.filter { v ->
            v.startsWith("/wiki/") && (if (v.contains(":")) (v.contains(":_")) else true)
        }
        filtered = filtered.slice(0..(if (filtered.size > 59) 59 else filtered.size - 1))
        filtered.forEach { v ->
            if (depth > 0) {
                extract(v.substring(6), depth - 1)
            }
            graph.addEdge(articleName, v.substring(6), 1.0)
        }
        println("found ${filtered.size} pages for $articleName")
        return graph
    }
}
