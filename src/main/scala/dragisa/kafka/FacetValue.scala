package dragisa.kafka

import com.sksamuel.avro4s._
import com.sksamuel.avro4s.kafka.GenericSerde
import io.circe.Json
import org.apache.avro.Schema
import org.apache.kafka.common.serialization.Serde

import java.time.ZonedDateTime
import java.util.UUID

final case class FacetValue(
    entityDefId: String,
    facetId: String,
    uuid: UUID,
    properties: Option[Json],
    createdAt: ZonedDateTime,
    updatedAt: ZonedDateTime
)

object FacetValue {
  import CirceAvro._
  import DateTimeAvro._
  implicit val snake: FieldMapper                    = SnakeCase
  implicit val faceValueAvroSchema: Schema           = AvroSchema[FacetValue]
  implicit val faceValueDecoder: Decoder[FacetValue] = Decoder[FacetValue]

  val facetValueSerde: Serde[FacetValue] = new GenericSerde[FacetValue](BinaryFormat)

}
