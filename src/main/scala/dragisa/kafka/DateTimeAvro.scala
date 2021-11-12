package dragisa.kafka

import com.sksamuel.avro4s._
import org.apache.avro.Schema

import java.time.ZonedDateTime
import scala.util.{Success, Try, Failure}

object DateTimeAvro {

  implicit val zonedDateTimeSchemaFor: SchemaFor[ZonedDateTime] =
    SchemaFor[ZonedDateTime](Schema.create(Schema.Type.STRING))

  implicit val zonedDateTimeEncoder: Encoder[ZonedDateTime] = new Encoder[ZonedDateTime] {
    override val schemaFor: SchemaFor[ZonedDateTime]  = zonedDateTimeSchemaFor
    override def encode(value: ZonedDateTime): String = value.toString
  }

  implicit val zonedDateTimeDecoder: Decoder[ZonedDateTime] = new Decoder[ZonedDateTime] {
    override val schemaFor: SchemaFor[ZonedDateTime] = zonedDateTimeSchemaFor
    override def decode(value: Any): ZonedDateTime   = Try(ZonedDateTime.parse(value.toString)) match {
      case Success(zdt) => zdt
      case Failure(err) =>
        throw new Avro4sDecodingException(s"Can't parse $value", value, this)
          .initCause(err)
    }
  }
}
