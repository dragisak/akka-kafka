package dragisa.kafka

import dragisa.kafka.config.KafkaConfig
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers._
import pureconfig.ConfigSource
import pureconfig._
import pureconfig.generic.auto._

import java.util.UUID

class FacetValueTest extends AnyWordSpec {
  private val kafkaConfig = ConfigSource.default.at("dragisa.kafka").loadOrThrow[KafkaConfig]
  "bytes" should {
    val bytes: Array[Byte] = Array(
      0, 0, 0, 10, -14, 38, 115, 105, 102, 116, 101, 114, 121, 95, 112, 114, 111, 100, 117, 99, 116, 95, 117, 115, 101,
      14, 100, 101, 102, 97, 117, 108, 116, 72, 97, 101, 56, 48, 101, 51, 51, 100, 45, 98, 55, 55, 100, 45, 52, 102, 48,
      50, 45, 98, 55, 99, 102, 45, 97, 56, 99, 48, 54, 55, 97, 55, 49, 49, 55, 54
    )

    "convert to key" in {
      val deser = FacetKey.facetKeySerde.deserializer()
      val key   = deser.deserialize(kafkaConfig.topic, bytes)
      key shouldBe FacetKey(
        entityDefId = "siftery",
        facetId = "default",
        uuid = UUID.randomUUID()
      )

    }
  }
}
