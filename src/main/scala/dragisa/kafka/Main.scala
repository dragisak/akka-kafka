package dragisa.kafka

import akka.actor.{ActorSystem, CoordinatedShutdown}
import akka.kafka.scaladsl._
import akka.kafka.{ConsumerSettings, Subscriptions}
import akka.stream.scaladsl._
import dragisa.kafka.config.KafkaConfig
import org.apache.kafka.clients.consumer.{ConsumerConfig, ConsumerRecord}
import org.slf4j.LoggerFactory
import pureconfig._
import pureconfig.generic.auto._

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
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

      logger.info(FacetKey.faceKeyAvroSchema.toString(true))
      logger.info(FacetValue.faceValueAvroSchema.toString(true))

      val consumerConfig: ConsumerSettings[FacetKey, FacetValue] = ConsumerSettings(
        system = system,
        keyDeserializer = FacetKey.facetKeySerde.deserializer(),
        valueDeserializer = FacetValue.facetValueSerde.deserializer()
      )
        .withBootstrapServers(kafkConfig.bootstrap)
        .withGroupId(kafkConfig.groupId)
        .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")

      val subscription = Subscriptions.topics(kafkConfig.topic)

      val source = Consumer.plainSource(consumerConfig, subscription)
      val sink   = Sink.foreach(logMesssage)
      val graph  = source.toMat(sink)(Consumer.DrainingControl.apply)

      logger.info("Starting")
      val control = graph.run()

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

      Await.result(system.whenTerminated, 1.minute)
      logger.info("Bye.")

  }

  private def logMesssage(msg: KafkaMessage): Unit = {
    logger.info(
      Option(msg.value())
        .flatMap(_.properties)
        .map(_.spaces2)
        .getOrElse("null")
    )
  }
}
