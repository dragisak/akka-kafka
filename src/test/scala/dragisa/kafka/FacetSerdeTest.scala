package dragisa.kafka

import dragisa.kafka.config.KafkaConfig
import io.circe.parser._
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers._
import pureconfig.ConfigSource
import pureconfig.generic.auto._

import java.time.ZonedDateTime
import java.util.UUID

class FacetSerdeTest extends AnyWordSpec {
  private val kafkaConfig = ConfigSource.default.at("dragisa.kafka").loadOrThrow[KafkaConfig]
  "key bytes" should {
    val bytes: Array[Byte] = Array(
      0, 0, 0, 10, -14, 38, 115, 105, 102, 116, 101, 114, 121, 95, 112, 114, 111, 100, 117, 99, 116, 95, 117, 115, 101,
      14, 100, 101, 102, 97, 117, 108, 116, 72, 97, 101, 56, 48, 101, 51, 51, 100, 45, 98, 55, 55, 100, 45, 52, 102, 48,
      50, 45, 98, 55, 99, 102, 45, 97, 56, 99, 48, 54, 55, 97, 55, 49, 49, 55, 54
    )

    "deserialized to FacetKey" in {
      val key      = FacetKey.serde.deserializer().deserialize(kafkaConfig.topic, bytes)
      val expected = FacetKey(
        entityDefId = "siftery_product_use",
        facetId = "default",
        uuid = UUID.fromString("ae80e33d-b77d-4f02-b7cf-a8c067a71176")
      )

      key shouldBe expected
    }
  }

  "value bytes" should {
    val bytes: Array[Byte] = Array(
      0, 0, 0, 11, 10, 14, 99, 111, 110, 116, 97, 99, 116, 14, 100, 101, 102, 97, 117, 108, 116, 72, 54, 97, 98, 56, 53,
      48, 102, 101, 45, 53, 102, 97, 102, 45, 52, 53, 48, 102, 45, 56, 97, 49, 55, 45, 101, 98, 54, 53, 57, 102, 101,
      100, 57, 101, 99, 48, 2, 52, 123, 34, 115, 111, 117, 114, 99, 101, 95, 116, 121, 112, 101, 34, 58, 32, 34, 108,
      101, 97, 100, 52, 49, 49, 34, 125, 48, 50, 48, 50, 49, 45, 48, 57, 45, 50, 50, 84, 48, 54, 58, 48, 50, 58, 51, 52,
      46, 55, 48, 55, 90, 48, 50, 48, 50, 49, 45, 48, 57, 45, 50, 50, 84, 48, 54, 58, 48, 50, 58, 51, 52, 46, 55, 48,
      55, 90
    )
    "deserialize to FacetValue" in {
      val value    = FacetValue.serde.deserializer().deserialize(kafkaConfig.topic, bytes)
      val expected = FacetValue(
        entityDefId = "contact",
        facetId = "default",
        uuid = UUID.fromString("6ab850fe-5faf-450f-8a17-eb659fed9ec0"),
        properties = parse("""{  "source_type" : "lead411" }""").toOption,
        createdAt = ZonedDateTime.parse("2021-09-22T06:02:34.707Z"),
        updatedAt = ZonedDateTime.parse("2021-09-22T06:02:34.707Z")
      )

      value shouldBe expected
    }
  }
}
