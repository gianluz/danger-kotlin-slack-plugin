@file:DependsOn("com.gianluz:danger-kotlin-slackalo-plugin:0.0.3")

import com.gianluz.dangerkotlin.slackalo.Slackalo
import systems.danger.kotlin.*

register plugin Slackalo

danger(args) {
    onGitHub {
        Slackalo.sendReport {
            slackWebHookUrl = System.getenv("SLACK_WEBHOOK_URL")
            commitCount = pullRequest.commitCount?:0
            pullRequestUrl = pullRequest.htmlURL
            pullRequestNumber = pullRequest.number.toString()
            pullRequestTitle = pullRequest.title
            author = Slackalo.Author(
                pullRequest.user.login,
                pullRequest.user.avatarUrl
            )
        }
    }
}