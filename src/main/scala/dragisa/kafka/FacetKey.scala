package dragisa.kafka

import com.sksamuel.avro4s._
import org.apache.avro.Schema

import java.util.UUID

final case class FacetKey(
    entityDefId: String,
    facetId: String,
    uuid: UUID
)

object FacetKey {
  implicit val snake: FieldMapper = SnakeCase

  implicit val faceKeyAvroSchema: Schema = AvroSchema[FacetKey]

}
