package dragisa.kafka

import io.circe.Json
import vulcan.Codec

import java.util.UUID

final case class FacetValue(
    uuid: UUID,
    entity_def_id: String,
    facet_id: String,
    properties: Json
)

object FacetValue {
  import vulcan.generic._
  import CirceAvro._

  implicit val facetValueCodec: Codec[FacetValue] = Codec.derive[FacetValue]
}
