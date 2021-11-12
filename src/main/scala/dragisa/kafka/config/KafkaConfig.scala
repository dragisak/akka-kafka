package dragisa.kafka.config

import pureconfig.ConfigReader.Result

final case class KafkaConfig(
    bootstrap: String,
    topic: String,
    groupId: String
)

object KafkaConfig {

  import pureconfig._
  import pureconfig.generic.auto._
  private val namespace = "dragisa.kafka"

  def load: Result[KafkaConfig] = source.load[KafkaConfig]
  def loadOrThrow: KafkaConfig  = source.loadOrThrow[KafkaConfig]

  private def source = ConfigSource.default.at(namespace)

}
