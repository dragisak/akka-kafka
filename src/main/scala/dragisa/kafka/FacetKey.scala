package dragisa.kafka

import com.sksamuel.avro4s._
import com.sksamuel.avro4s.kafka.GenericSerde

import java.util.UUID

/**
 * {{{
 * {
 *   "type": "record",
 *   "name": "Key",
 *   "namespace": "entityservice.848.public.node_facets_v2",
 *   "fields": [
 *     {
 *       "name": "entity_def_id",
 *       "type": "string"
 *     },
 *     {
 *       "name": "facet_id",
 *       "type": "string"
 *     },
 *     {
 *       "name": "uuid",
 *       "type": {
 *         "type": "string",
 *         "connect.version": 1,
 *         "connect.name": "io.debezium.data.Uuid"
 *       }
 *     }
 *   ],
 *   "connect.name": "entityservice.848.public.node_facets_v2.Key"
 * }
 * }}}
 */
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

  val serde: GenericSerde[FacetKey] = ConfluentKafkaSerde[FacetKey]
}
