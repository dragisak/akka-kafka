package dragisa.kafka

import akka.actor.ActorSystem
import akka.kafka.scaladsl.Consumer
import akka.kafka.{ConsumerSettings, Subscriptions}
import akka.stream.scaladsl.Source
import dragisa.kafka.config.KafkaConfig
import org.apache.kafka.clients.consumer.{ConsumerConfig, ConsumerRecord}
import pureconfig._
import pureconfig.generic.auto._

object Main extends App {
  ConfigSource.default.at("dragisa.kafka").load[KafkaConfig] match {
    case Left(error)       =>
      throw new RuntimeException(s"Bad config: ${error.toList.map(_.description).mkString("\n")}")
      sys.exit(-1)
    case Right(kafkConfig) =>
      implicit val system: ActorSystem = ActorSystem("Main")
      import system.dispatcher
      val config                       = system.settings.config.getConfig("akka.kafka.consumer")

      val consumerConfig: ConsumerSettings[FacetKey, FacetValue] = ConsumerSettings(
        config = config,
        keyDeserializer = FacetKey.facetKeySerde.deserializer(),
        valueDeserializer = FacetValue.facetValueSerde.deserializer()
      )
        .withBootstrapServers(kafkConfig.bootstrap)
        .withGroupId(kafkConfig.groupId)
        .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")

      val subscription                                                             = Subscriptions.topics(kafkConfig.topic)
      val consumer: Source[ConsumerRecord[FacetKey, FacetValue], Consumer.Control] =
        Consumer.plainSource(consumerConfig, subscription)

      val f = consumer.runForeach(v => println(v.value().properties.spaces2))

      f.onComplete(_ => sys.exit(0))
  }
}
