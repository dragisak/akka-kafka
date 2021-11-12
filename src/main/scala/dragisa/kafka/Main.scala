package dragisa.kafka

import akka.actor.{ActorSystem, CoordinatedShutdown}
import akka.kafka.scaladsl._
import akka.kafka.{ConsumerSettings, Subscriptions}
import akka.stream.scaladsl._
import dragisa.kafka.config.KafkaConfig
import org.apache.kafka.clients.consumer.{ConsumerConfig, ConsumerRecord}
import org.slf4j.LoggerFactory

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.util.{Failure, Success}

object Main extends App {

  private type KafkaMessage = ConsumerRecord[FacetKey, FacetValue]

  private val logger = LoggerFactory.getLogger(getClass)

  KafkaConfig.load match {
    case Left(error)       =>
      throw new RuntimeException(s"Bad config: ${error.toList.map(_.description).mkString("\n")}")
      sys.exit(-1)
    case Right(kafkConfig) =>
      implicit val system: ActorSystem = ActorSystem("Main")

      logger.info(FacetKey.serde.schema.toString(true))
      logger.info(FacetValue.serde.schema.toString(true))

      val consumerConfig = ConsumerSettings(
        system = system,
        keyDeserializer = FacetKey.serde.deserializer(),
        valueDeserializer = FacetValue.serde.deserializer()
      )
        .withBootstrapServers(kafkConfig.bootstrap)
        .withGroupId(kafkConfig.groupId)
        .withStopTimeout(Duration.Zero)
        .withPollTimeout(5.second)
        .withMetadataRequestTimeout(5.second)
        .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
        .withProperty(ConsumerConfig.REQUEST_TIMEOUT_MS_CONFIG, "60000")

      val subscription = Subscriptions.topics(kafkConfig.topic)

      val source = Consumer.plainSource(consumerConfig, subscription)
      val sink   = Sink.foreach(logMesssage)
      val graph  = source
        .log("msg")
        // .map(deserialize)
        .toMat(sink)(Keep.both)

      logger.info("Starting")
      val (control, completion) = graph.run()

      CoordinatedShutdown(system)
        .addTask(CoordinatedShutdown.PhaseServiceRequestsDone, "complete hdfs sinks") { () =>
          val f = control.drainAndShutdown(completion)
          f.onComplete {
            case Success(_)   =>
              logger.info("Leaving ...")
            case Failure(err) =>
              logger.error("Ouch!", err)
          }
          f
        }

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
