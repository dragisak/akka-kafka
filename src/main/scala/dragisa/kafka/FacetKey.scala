package dragisa.kafka

import com.sksamuel.avro4s._
import com.sksamuel.avro4s.kafka.GenericSerde
import org.apache.avro.Schema
import org.apache.kafka.common.serialization.Serde

import java.util.UUID

final case class FacetKey(
    entityDefId: String,
    facetId: String,
    uuid: UUID
)

object FacetKey {
  implicit val snake: FieldMapper = SnakeCase

  implicit val faceKeyAvroSchema: Schema         = AvroSchema[FacetKey]
  implicit val faceKeyDecoder: Decoder[FacetKey] = Decoder[FacetKey]

  val facetKeySerde: Serde[FacetKey] = new GenericSerde[FacetKey](BinaryFormat)
}
