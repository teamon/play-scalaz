## Scalaz integration for Play 2.0

## Instalation

Add `play-scalaz` to your `project/Build.scala` file

``` scala
val appDependencies = Seq(
  "eu.teamon" %% "play-scalaz" % "0.1.0-SNAPSHOT"
)

val main = PlayProject(appName, appVersion, appDependencies, mainLang = SCALA).settings(
  resolvers += "scalajars.org repo" at "http://scalajars.org/repository"
)
```
