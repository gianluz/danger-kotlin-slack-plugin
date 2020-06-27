<p align="center">
<img src="https://raw.githubusercontent.com/gianluz/slackalo/master/design/cover.png" /></br></br>
</p>

# danger-kotlin-slackalo-plugin

A plugin for Danger Kotlin that enables to send messages via [Slackalo](https://github.com/gianluz/slackalo)

## Setup

Install and run [Danger Kotlin] as normal and in your `Dangerfile.df.kts` add the following dependency:
```kotlin
@file:DependsOn("com.gianluz:danger-kotlin-slackalo-plugin:0.0.3")
```
Then register your plugin before the `danger` initialisation and use the plugin:
```kotlin
register plugin Slackalo

val danger = Danger(args)

// Default report
Slackalo.sendReport {
        slackWebHookUrl = "https://hooks.slack.com/services/myGeneratedWebHooKUrl"
        commitCount = pullRequest.commitCount
        pullRequestNumber = pullRequest.number.toString()
        pullRequestTitle = pullRequest.title
        pullRequestUrl = pullRequest.htmlURL
        author = Slackalo.Author(
            name = pullRequest.user.login,
            avatarUrl = "https://avatars3.githubusercontent.com/u/29659183?s=460&u=5ea9c242e92f1baad8eb731d1eb742f19d056b4f&v=4"
        )
    }
```
You can report more than one lint file.

You can also keep tidy your `DangerFile.df.kts` using the following block:
```kotlin
androidLint {
    [...]
}
```
Or make your own custom report by manipulating all the issues found, for example failing the build at the first `Fatal` found in a specific module.
```kotlin
androidLint {
        // Fail for each Fatal in a single module
        val moduleLintFilePaths = find(
            moduleDir,
            "lint-results-debug.xml",
            "lint-results-release.xml"
        ).toTypedArray()

        parseAllDistinct(*moduleLintFilePaths).forEach {
            if(it.severity == "Fatal")
                fail(
                    "Danger lint check failed: ${it.message}", 
                    it.location.file.replace(System.getProperty("user.dir"), ""), 
                    Integer.parseInt(it.location.line)
                )
        }
    }
```