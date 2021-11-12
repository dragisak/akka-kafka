package dragisa.kafka

import com.sksamuel.avro4s._
import com.sksamuel.avro4s.kafka.GenericSerde
import io.circe.Json
import org.apache.avro.Schema
import org.apache.kafka.common.serialization.Serde

import java.time.ZonedDateTime
import java.util.UUID

/** {{{
  * {
  *   "type": "record",
  *   "name": "Value",
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
  *     },
  *     {
  *       "name": "properties",
  *       "type": [
  *         "null",
  *         {
  *           "type": "string",
  *           "connect.version": 1,
  *           "connect.name": "io.debezium.data.Json"
  *         }
  *       ],
  *       "default": null
  *     },
  *     {
  *       "name": "created_at",
  *       "type": {
  *         "type": "string",
  *         "connect.version": 1,
  *         "connect.name": "io.debezium.time.ZonedTimestamp"
  *       }
  *     },
  *     {
  *       "name": "updated_at",
  *       "type": {
  *         "type": "string",
  *         "connect.version": 1,
  *         "connect.name": "io.debezium.time.ZonedTimestamp"
  *       }
  *     }
  *   ],
  *   "connect.name": "entityservice.848.public.node_facets_v2.Value"
  * }
  * }}}
  */

final case class FacetValue(
    entityDefId: String,
    facetId: String,
    uuid: UUID,
    properties: Option[Json] = None,
    createdAt: ZonedDateTime,
    updatedAt: ZonedDateTime
)

object FacetValue {
  import CirceAvro._
  import DateTimeAvro._

  private implicit val snake: FieldMapper = SnakeCase

//  val faceValueAvroSchema: Schema = AvroSchema[FacetValue]
//  implicit val faceValueEncoder: Encoder[FacetValue]      = Encoder[FacetValue]
//  implicit val faceValueDecoder: Decoder[FacetValue]      = Decoder[FacetValue]

  val facetValueSerde: GenericSerde[FacetValue] = new GenericSerde[FacetValue](BinaryFormat)

}
