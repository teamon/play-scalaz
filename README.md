## Scalaz integration for Play 2.0

## Instalation

Add `play-scalaz` to your `project/Build.scala` file

``` scala
val appDependencies = Seq(
  "eu.teamon" %% "play-scalaz" % "0.1.2-SNAPSHOT"
)

val main = PlayProject(appName, appVersion, appDependencies, mainLang = SCALA).settings(
  resolvers += "scalajars repo" at "http://scalajars.org/repository",
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


### Promise Monad

Let's say there is code like:
```scala
def authenticate(code: String): Promise[Option[Projects]] = requestAccessToken(code).flatMap { tokenOpt =>
  tokenOpt.map { token => 
    requestAccessToken(token).flatMap { userOpt =>
      userOpt.map { user =>
        requestProject(user)
      } getOrElse Promise.pure(None)
    }
  } getOrElse Promise.pure(None)
}

def requestAccessToken(code: String): Promise[Option[String]]
def requestUserInfo(accessToken: String): Promise[Option[UserInfo]]
def requestProjects(user: UserInfo): Promise[Option[Projects]]
```

it gets even more bloated when number of calls grows.
But with `Promise` being `Monad`:

```scala
def authenticate(code: String): Promise[Option[UserInfo]] = (for {
  accessToken <- OptionT(requestAccessToken(code))
  userInfo    <- OptionT(requestUserInfo(accessToken))
  projects    <- OptionT(requestProject(userInfo))
} yield projects).run
```

profit!
