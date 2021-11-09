package dragisa.kafka.config

final case class KafkaConfig(
    bootstrap: String,
    schemaRegistry: String,
    topic: String,
    groupId: String
)
