package play.scalaz

import _root_.scalaz._
import _root_.scalaz.Scalaz._
import play.api.libs.json._

object json {
  type VA[A] = ValidationNEL[String, A]

  type Writez[A] = Writes[A]
  trait Readz[A] { def reads(js: JsValue): VA[A] }

  trait Formatz[A] extends Readz[A] with Writez[A]

  case class DerivedFormatz[A](readz: Readz[A], writez: Writez[A]) extends Formatz[A] {
    def reads(js: JsValue) = readz.reads(js)
    def writes(a: A) = writez.writes(a)
  }

  def writez[A](f: A => List[(String, JsValue)]): Writes[A] = new Writes[A] {
    def writes(a: A) = toJson(f(a).toMap)
  }

  def writezAs[A,B](f: A => B)(implicit wz: Writes[B]): Writes[A] = new Writes[A] {
    def writes(a: A) = wz.writes(f(a))
  }

  implicit def sops(key: String) = new {
    def <>[B:Writes](v: B) = (key, toJson(v))
  }

  def field[A](key: String, validators: (A => Option[String])*)(implicit readz: Readz[A]): JsValue => VA[A] = js => {
    (js \ key) match {
      case JsUndefined(error) => "Field '%s' not found".format(key).failure.toValidationNEL
      case value => readz.reads(value).fold(
        e => e.map(("Field '%s': ".format(key))+).failure,
        s => validators.map(_(s)).flatten match {
          case Nil => s.success
          case x :: xs => NonEmptyList(x, xs:_*).failure
        }
      )
    }
  }

  val nonEmptyString: String => Option[String] = s => if(s.trim == "") "must not be empty".some else none


  def fieldOpt[A](key: String)(implicit readz: Readz[A]): JsValue => VA[Option[A]] = js => {
    readz.reads(js \ key).fold(
      e => none[A],
      s => some(s)
    ).success
  }

  def toJson[A](a: A)(implicit wz: Writes[A]) = wz.writes(a)
  def fromJson[A](js: JsValue)(implicit rz: Readz[A]) = rz.reads(js)

  def formatz2[A, T1:Readz:Writez, T2:Readz:Writez](k1: String, k2: String)(apply: (T1, T2) => A)(unapply: A => (T1, T2)): Formatz[A] = DerivedFormatz(
    readz2[A, T1, T2](k1, k2)(apply), writez2[A, T1, T2](k1, k2)(unapply)
  )

  def formatz3[A, T1:Readz:Writez, T2:Readz:Writez, T3:Readz:Writez](k1: String, k2: String, k3: String)(apply: (T1, T2, T3) => A)(unapply: A => (T1, T2, T3)): Formatz[A] = DerivedFormatz(
    readz3[A, T1, T2, T3](k1, k2, k3)(apply), writez3[A, T1, T2, T3](k1, k2, k3)(unapply)
  )


  def readz2[A, T1:Readz, T2:Readz](k1: String, k2: String)(f: (T1,T2) => A): Readz[A] = new Readz[A]{
    def reads(js: JsValue) = {
      val data = for {
        f1 <- field[T1](k1)
        f2 <- field[T2](k2)
      } yield Apply[VA].apply(f1, f2)(f)
      data(js)
    }
  }

  def readz3[A, T1:Readz, T2:Readz, T3:Readz](k1: String, k2: String, k3: String)(f: (T1,T2,T3) => A): Readz[A] = new Readz[A]{
    def reads(js: JsValue) = {
      val data = for {
        f1 <- field[T1](k1)
        f2 <- field[T2](k2)
        f3 <- field[T3](k3)
      } yield Apply[VA].apply(f1, f2, f3)(f)
      data(js)
    }
  }


  def writez1[A, T1:Writes](k1: String)(f: A => T1): Writes[A] = writez(a => {
    val r = f(a)
    List(k1 <> r)
  })

  def writez2[A, T1:Writes, T2:Writes](k1: String, k2: String)(f: A => (T1,T2)): Writes[A] = writez(a => {
    val r = f(a)
    List(k1 <> r._1, k2 <> r._2)
  })

  def writez3[A, T1:Writes, T2:Writes, T3:Writes](k1: String, k2: String, k3: String)(f: A => (T1,T2,T3)): Writes[A] = writez(a => {
    val r = f(a)
    List(k1 <> r._1, k2 <> r._2, k3 <> r._3)
  })

  def writez4[A, T1:Writes, T2:Writes, T3:Writes, T4:Writes](k1: String, k2: String, k3: String, k4: String)(f: A => (T1,T2,T3,T4)): Writes[A] = writez(a => {
    val r = f(a)
    List(k1 <> r._1, k2 <> r._2, k3 <> r._3, k4 <> r._4)
  })

  def writez5[A, T1:Writes, T2:Writes, T3:Writes, T4:Writes, T5:Writes](k1: String, k2: String, k3: String, k4: String, k5: String)(f: A => (T1,T2,T3,T4,T5)): Writes[A] = writez(a => {
    val r = f(a)
    List(k1 <> r._1, k2 <> r._2, k3 <> r._3, k4 <> r._4, k5 <> r._5)
  })

  def writez6[A, T1:Writes, T2:Writes, T3:Writes, T4:Writes, T5:Writes, T6:Writes](k1: String, k2: String, k3: String, k4: String, k5: String, k6: String)(f: A => (T1,T2,T3,T4,T5,T6)): Writes[A] = writez(a => {
    val r = f(a)
    List(k1 <> r._1, k2 <> r._2, k3 <> r._3, k4 <> r._4, k5 <> r._5, k6 <> r._6)
  })

  def writez7[A, T1:Writes, T2:Writes, T3:Writes, T4:Writes, T5:Writes, T6:Writes, T7:Writes](k1: String, k2: String, k3: String, k4: String, k5: String, k6: String, k7: String)(f: A => (T1,T2,T3,T4,T5,T6,T7)): Writes[A] = writez(a => {
    val r = f(a)
    List(k1 <> r._1, k2 <> r._2, k3 <> r._3, k4 <> r._4, k5 <> r._5, k6 <> r._6, k7 <> r._7)
  })

  def writez8[A, T1:Writes, T2:Writes, T3:Writes, T4:Writes, T5:Writes, T6:Writes, T7:Writes, T8:Writes](k1: String, k2: String, k3: String, k4: String, k5: String, k6: String, k7: String, k8: String)(f: A => (T1,T2,T3,T4,T5,T6,T7,T8)): Writes[A] = writez(a => {
    val r = f(a)
    List(k1 <> r._1, k2 <> r._2, k3 <> r._3, k4 <> r._4, k5 <> r._5, k6 <> r._6, k7 <> r._7, k8 <> r._8)
  })


  protected def SimpleReadz[A](f: JsValue => ValidationNEL[String, A]): Readz[A] = new Readz[A] {
    def reads(js: JsValue) = f(js)
  }

  object Writes extends DefaultWrites
  object Readz extends DefaultReadz
  object Formatz extends DefaultReadz with DefaultWrites

  trait DefaultReadz {
    implicit val ShortReadz = SimpleReadz[Short]({
      case JsNumber(n) => n.toShort.success
      case _ => "Number expected".failure.toValidationNEL
    })

    implicit val IntReadz = SimpleReadz[Int]({
      case JsNumber(n) => n.toInt.success
      case _ => "Number expected".failure.toValidationNEL
    })

    implicit val LongReadz = SimpleReadz[Long]({
      case JsNumber(n) => n.toLong.success
      case _ => "Number expected".failure.toValidationNEL
    })

    implicit val FloatReadz = SimpleReadz[Float]({
      case JsNumber(n) => n.toFloat.success
      case _ => "Number expected".failure.toValidationNEL
    })

    implicit val DoubleReadz = SimpleReadz[Double]({
      case JsNumber(n) => n.toDouble.success
      case _ => "Number expected".failure.toValidationNEL
    })

    implicit val BigDecimalReadz = SimpleReadz[BigDecimal]({
      case JsNumber(n) => n.success
      case _ => "Number expected".failure.toValidationNEL
    })

    implicit val BooleanReadz = SimpleReadz[Boolean]({
      case JsBoolean(b) => b.success
      case _ => "Boolean expected".failure.toValidationNEL
    })

    implicit val StringReadz = SimpleReadz[String]({
      case JsString(s) => s.success
      case _ => "String expected".failure.toValidationNEL
    })

    implicit def OptionReads[A](implicit rz: Readz[A]): Readz[Option[A]] = new Readz[Option[A]]{
      def reads(js: JsValue) = rz.reads(js).fold(_ => none[A], some).success
    }


  // /**
  //  * Deserializer for Map[String,V] types.
  //  */
  // implicit def mapReads[V](implicit fmtv: Reads[V]): Reads[collection.immutable.Map[String, V]] = new Reads[collection.immutable.Map[String, V]] {
  //   def reads(json: JsValue) = json match {
  //     case JsObject(m) => m.map { case (k, v) => (k -> fromJson[V](v)(fmtv)) }.toMap
  //     case _ => throw new RuntimeException("Map expected")
  //   }
  // }

  // /**
  //  * Generic deserializer for collections types.
  //  */
    implicit def listReadz[A](implicit ra: Readz[A]) = new Readz[List[A]] {
      def reads(json: JsValue) = json match {
        case JsArray(ts) => ts.toList.map(fromJson[A]).sequence[VA, A]
        case _ => "Collection expected".failure.toValidationNEL
      }
    }

  // /**
  //  * Deserializer for Array[T] types.
  //  */
  // implicit def arrayReads[T: Reads: Manifest]: Reads[Array[T]] = new Reads[Array[T]] {
  //   def reads(json: JsValue) = json.as[List[T]].toArray
  // }

  // /**
  //  * Deserializer for JsValue.
  //  */
  // implicit object JsValueReads extends Reads[JsValue] {
  //   def reads(json: JsValue) = json
  // }

  // /**
  //  * Deserializer for JsObject.
  //  */
  // implicit object JsObjectReads extends Reads[JsObject] {
  //   def reads(json: JsValue) = json match {
  //     case o: JsObject => o
  //     case _ => throw new RuntimeException("JsObject expected")
  //   }
  // }



  // implicit def OptionReads[T](implicit fmt: Reads[T]): Reads[Option[T]] = new Reads[Option[T]] {
  //   import scala.util.control.Exception._
  //   def reads(json: JsValue) = catching(classOf[RuntimeException]).opt(fmt.reads(json))
  // }

  }

  implicit def ListStringJsValueWritez: Writes[List[(String, JsValue)]] = writezAs(list => list.toMap)

  implicit def NonEmptyListWritez[A:Writes]: Writes[NonEmptyList[A]] = writezAs(_.list)


}
