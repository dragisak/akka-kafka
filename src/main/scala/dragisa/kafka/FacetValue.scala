package dragisa.kafka

import com.sksamuel.avro4s._
import com.sksamuel.avro4s.kafka.GenericSerde
import io.circe.Json
import org.apache.avro.Schema
import org.apache.kafka.common.serialization.Serde

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

  val facetValueSerde: Serde[FacetValue] = new GenericSerde[FacetValue](BinaryFormat)

}
