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

## Features

### Json deserialization

Play's default json API:
```scala
trait Reads[A] {
  def reads(js: JsValue): A
}


someJson.as[A]    // A or Exception
someJson.asOpt[A] // Option[A]

```

will throw exception in case of invalid json input, or when using `asOpt` will return `Option[A]` which contains no information about what went wrong.

play-scalaz provides
```scala
trait Readz[A] {
  def reads(js: JsValue): Validation[NonEmptyList[String], A]
}

case class Foo(a: Int, b: String)

implicit val FooReadz = readz2("a", "b")(Foo)


fromJson[Foo](someJson) // Validation[NonEmptyList[String], Foo]
```

which will return either the value or list of errors
