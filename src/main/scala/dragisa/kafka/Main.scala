package dragisa.kafka

import akka.Done
import akka.actor.{ActorSystem, CoordinatedShutdown}
import akka.kafka.scaladsl._
import akka.kafka.{ConsumerSettings, Subscriptions}
import akka.stream.scaladsl._
import dragisa.kafka.config.KafkaConfig
import org.apache.kafka.clients.consumer.{ConsumerConfig, ConsumerRecord}
import org.slf4j.LoggerFactory
import pureconfig._
import pureconfig.generic.auto._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

object Main extends App {

  private type KafkaMessage = ConsumerRecord[FacetKey, FacetValue]

  private val logger = LoggerFactory.getLogger(getClass)

  ConfigSource.default.at("dragisa.kafka").load[KafkaConfig] match {
    case Left(error)       =>
      throw new RuntimeException(s"Bad config: ${error.toList.map(_.description).mkString("\n")}")
      sys.exit(-1)
    case Right(kafkConfig) =>
      implicit val system: ActorSystem = ActorSystem("Main")

      val consumerConfig: ConsumerSettings[FacetKey, FacetValue] = ConsumerSettings(
        system = system,
        keyDeserializer = FacetKey.facetKeySerde.deserializer(),
        valueDeserializer = FacetValue.facetValueSerde.deserializer()
      )
        .withBootstrapServers(kafkConfig.bootstrap)
        .withGroupId(kafkConfig.groupId)
        .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")

      val subscription = Subscriptions.topics(kafkConfig.topic)

      logger.info(FacetKey.faceKeyAvroSchema.toString(true))
      logger.info(FacetValue.faceValueAvroSchema.toString(true))

      val source: Source[KafkaMessage, Consumer.Control] =
        Consumer.plainSource(consumerConfig, subscription)

      val sink: Sink[KafkaMessage, Future[Done]] =
        Sink
          .foreach(v =>
            logger.info(
              Option(v.value())
                .flatMap(_.properties)
                .map(_.spaces2)
                .getOrElse("null")
            )
          )

      logger.info("Starting")

      val flow = source
        .toMat(sink)(Consumer.DrainingControl.apply)

      val control = flow.run()

      CoordinatedShutdown(system)
        .addTask(CoordinatedShutdown.PhaseServiceRequestsDone, "complete hdfs sinks") { () =>
          val f = control.drainAndShutdown()
          f.onComplete {
            case Success(_)   =>
              logger.info("Leaving ...")
            case Failure(err) =>
              logger.error("Ouch!", err)
          }
          f
        }

  }
}
