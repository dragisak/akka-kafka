package dragisa.kafka

import io.circe.Json
import io.circe.parser._
import vulcan.{AvroError, Codec}

object CirceAvro {

  implicit val circeAvroCodec: Codec[Json] = Codec[String].imapError(
    parse(_).left.map(parsingFailure => AvroError(parsingFailure.message))
  )(json => json.noSpaces)

}
