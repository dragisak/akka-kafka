package dragisa.kafka

import vulcan.Codec

import java.util.UUID

final case class FacetKey(
    entity_def_id: String,
    facet_id: String,
    uuid: UUID
)

object FacetKey {
  import vulcan.generic._

  implicit val faceKeyCodec: Codec[FacetKey] = Codec.derive[FacetKey]

}
