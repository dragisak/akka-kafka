package dragisa.kafka

import com.sksamuel.avro4s._
import io.circe.Json
import org.apache.avro.Schema

import java.util.UUID

final case class FacetValue(
    uuid: UUID,
    entityDefId: String,
    facetId: String,
    properties: Json
)

object FacetValue {
  import CirceAvro._
  implicit val snake: FieldMapper          = SnakeCase
  implicit val faceValueAvroSchema: Schema = AvroSchema[FacetValue]
}
