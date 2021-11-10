package dragisa.kafka

import com.sksamuel.avro4s.{Avro4sDecodingException, Decoder, Encoder, SchemaFor}
import io.circe.Json
import io.circe.parser._
import org.apache.avro.Schema

object CirceAvro {

  implicit val circeJsonSchemaFor: SchemaFor[Json] = SchemaFor[Json](Schema.create(Schema.Type.STRING))

  implicit val circeEncoder: Encoder[Json] = new Encoder[Json] {
    override val schemaFor: SchemaFor[Json]  = circeJsonSchemaFor
    override def encode(value: Json): String = value.noSpaces
  }

  implicit val circeDecoder: Decoder[Json] = new Decoder[Json] {
    override val schemaFor: SchemaFor[Json] = circeJsonSchemaFor
    override def decode(value: Any): Json   = value match {
      case str: String =>
        parse(str) match {
          case Left(parsingError) =>
            throw new Avro4sDecodingException(s"Can't parse Json: ${parsingError.message}", value, this)
          case Right(json)        => json
        }
      case _           => throw new Avro4sDecodingException("Type must be String", value, this)
    }

  }
}
