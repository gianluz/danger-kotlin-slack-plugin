package com.gianluz.dangerkotlin.slackalo

import com.gianluz.slackalo.*
import systems.danger.kotlin.sdk.DangerPlugin

object Slackalo : DangerPlugin() {

    override val id: String
        get() = this.javaClass.name

    private val webHookClient by lazy { DefaultWebHookClient() }

    fun sendReport(report: Report.() -> Unit) {
        with(Report().apply(report)) {
            val message = slackMessage {
                blocks {
                    imageMarkdown(
                        "*Pull Request* <$pullRequestUrl|#$pullRequestNumber $pullRequestTitle>\n*Author: ${author.name}*\n$commitCount commits\n" +
                                if (context.fails.isEmpty()) {
                                    "Danger Kotlin succeeded all checks :white_check_mark:"
                                } else {
                                    "Danger Kotlin failed :x:"
                                },
                        author.avatarUrl,
                        author.name
                    )
                    if (context.fails.isNotEmpty()) {
                        markdown(":x: *Failures*")
                        fields {
                            context.fails.forEach { violation ->
                                markdown(violation.message + (violation.file?.let { " - file `$it`" }
                                    ?: "") + (violation.line?.let { " line: `$it`" } ?: ""))
                            }
                        }
                    }
                    if (context.warnings.isNotEmpty()) {
                        markdown(":warning: *Warnings*")
                        fields {
                            context.warnings.forEach { violation ->
                                markdown(violation.message + (violation.file?.let { " - file `$it`" }
                                    ?: "") + (violation.line?.let { " line: `$it`" } ?: ""))
                            }
                        }
                    }
                    val messages = context.messages.plus(context.markdowns)
                    if (messages.isNotEmpty()) {
                        markdown(":email: *Messages:*")
                        fields {
                            context.messages.forEach { violation ->
                                markdown(violation.message + (violation.file?.let { " - file `$it`" }
                                    ?: "") + (violation.line?.let { " line: `$it`" } ?: ""))
                            }
                        }
                    }
                }
            }

            val statusCode = webHookClient.sendWebHook(slackWebHookUrl, message)
            if (statusCode.value != 200) {
                context.warn("Danger Kotlin Slackalo plugin failed to send the report via slack")
            }
        }
    }

    class Report {
        lateinit var slackWebHookUrl: String
        lateinit var pullRequestUrl: String
        lateinit var pullRequestNumber: String
        lateinit var pullRequestTitle: String
        lateinit var author: Author
        var commitCount: Int = 0
    }

    data class Author(
        val name: String,
        val avatarUrl: String
    )
}