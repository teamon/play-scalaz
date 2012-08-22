package play

import _root_.scalaz._
import _root_.scalaz.Scalaz._
import play.api.libs.concurrent.Promise

package object scalaz {

  implicit val PromiseInstance = new Monad[Promise] {
    // override def map[A,B](fa: Promise[A])(f: A => B) = fa.map(f)
    def point[A](a: => A) = Promise.pure(a)
    def bind[A,B](fa: Promise[A])(f: A => Promise[B]) = fa.flatMap(f)
  }
}
