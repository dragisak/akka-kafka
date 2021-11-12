package dragisa.kafka.config

final case class KafkaConfig(
    bootstrap: String,
    topic: String,
    groupId: String
)
