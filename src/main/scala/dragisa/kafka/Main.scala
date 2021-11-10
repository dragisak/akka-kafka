package dragisa.kafka

import akka.actor.ActorSystem
import akka.kafka.scaladsl.Consumer
import akka.kafka.{ConsumerSettings, Subscriptions}
import akka.stream.scaladsl.Source
import dragisa.kafka.config.KafkaConfig
import org.apache.kafka.clients.consumer.{ConsumerConfig, ConsumerRecord}
import org.slf4j.LoggerFactory
import pureconfig._
import pureconfig.generic.auto._

object Main extends App {

  private val logger = LoggerFactory.getLogger(getClass)

  ConfigSource.default.at("dragisa.kafka").load[KafkaConfig] match {
    case Left(error)       =>
      throw new RuntimeException(s"Bad config: ${error.toList.map(_.description).mkString("\n")}")
      sys.exit(-1)
    case Right(kafkConfig) =>
      implicit val system: ActorSystem = ActorSystem("Main")
      import system.dispatcher

      val consumerConfig: ConsumerSettings[FacetKey, FacetValue] = ConsumerSettings(
        system = system,
        keyDeserializer = FacetKey.facetKeySerde.deserializer(),
        valueDeserializer = FacetValue.facetValueSerde.deserializer()
      )
        .withBootstrapServers(kafkConfig.bootstrap)
        .withGroupId(kafkConfig.groupId)
        .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")

      val subscription = Subscriptions.topics(kafkConfig.topic)

      val consumer: Source[ConsumerRecord[FacetKey, FacetValue], Consumer.Control] =
        Consumer.plainSource(consumerConfig, subscription)

      logger.info("Starting")
      val f = consumer.runForeach(v => logger.info(v.value().properties.spaces2))

      f.onComplete(_ => logger.info("Bye"))

      logger.info("End")
  }
}
