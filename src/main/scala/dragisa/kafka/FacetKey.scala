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
  private implicit val snake: FieldMapper = SnakeCase

//  val faceKeyAvroSchema: Schema = AvroSchema[FacetKey]
//  implicit val faceKeyDecoder: Decoder[FacetKey]      = Decoder[FacetKey]
//  implicit val faceKeyEncoder: Encoder[FacetKey]      = Encoder[FacetKey]

  val facetKeySerde: GenericSerde[FacetKey] = new GenericSerde[FacetKey](BinaryFormat)
}
