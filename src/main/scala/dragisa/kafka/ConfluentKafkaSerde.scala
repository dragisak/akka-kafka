package dragisa.kafka

import com.sksamuel.avro4s.{AvroFormat, BinaryFormat, Decoder, Encoder, SchemaFor}
import com.sksamuel.avro4s.kafka.GenericSerde

/** See https://docs.confluent.io/platform/current/schema-registry/serdes-develop/index.html#wire-format
  */
class ConfluentKafkaSerde[T >: Null: SchemaFor: Encoder: Decoder](avroFormat: AvroFormat = BinaryFormat)
    extends GenericSerde[T](avroFormat) {
  override def deserialize(topic: String, data: Array[Byte]): T = {
    if (data == null) {
      null
    } else
      super.deserialize(topic, data.drop(5))
  }

  private val dummyPrefix: Array[Byte] = Array(
    0, // MagicNumber always 0
    0,
    0,
    0,
    0
  )

  override def serialize(topic: String, data: T): Array[Byte] = {
    val bytes = super.serialize(topic, data)
    if (bytes == null) {
      null
    } else {
      dummyPrefix ++ bytes
    }
  }
}

object ConfluentKafkaSerde {
  def apply[T >: Null: SchemaFor: Encoder: Decoder](avroFormat: AvroFormat = BinaryFormat): GenericSerde[T] =
    new ConfluentKafkaSerde[T](avroFormat)
}
