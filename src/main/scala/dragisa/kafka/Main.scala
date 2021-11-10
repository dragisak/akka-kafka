package dragisa.kafka

import akka.actor.{ActorSystem, CoordinatedShutdown}
import akka.kafka.scaladsl._
import akka.kafka.{ConsumerSettings, Subscriptions}
import akka.stream.scaladsl._
import com.sksamuel.avro4s.AvroInputStream
import dragisa.kafka.config.KafkaConfig
import org.apache.kafka.clients.consumer.{ConsumerConfig, ConsumerRecord}
import org.apache.kafka.common.serialization.ByteArrayDeserializer
import org.slf4j.LoggerFactory
import pureconfig._
import pureconfig.generic.auto._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

object Main extends App {

  private val logger = LoggerFactory.getLogger(getClass)

  ConfigSource.default.at("dragisa.kafka").load[KafkaConfig] match {
    case Left(error)       =>
      throw new RuntimeException(s"Bad config: ${error.toList.map(_.description).mkString("\n")}")
      sys.exit(-1)
    case Right(kafkConfig) =>
      implicit val system: ActorSystem = ActorSystem("Main")

      logger.info(FacetKey.faceKeyAvroSchema.toString(true))
      logger.info(FacetValue.faceValueAvroSchema.toString(true))

      val consumerConfig: ConsumerSettings[Array[Byte], Array[Byte]] = ConsumerSettings(
        system = system,
        keyDeserializer = new ByteArrayDeserializer,
        valueDeserializer = new ByteArrayDeserializer
      )
        .withBootstrapServers(kafkConfig.bootstrap)
        .withGroupId(kafkConfig.groupId)
        .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")

      val subscription = Subscriptions.topics(kafkConfig.topic)

      val source = Consumer.plainSource(consumerConfig, subscription)
      val sink   = Sink.foreach(logByteMessage)
      val graph  = source
        .log("msg")
        // .map(deserialize)
        .toMat(sink)(Consumer.DrainingControl.apply)

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

  }

  private def logMessage(msg: Option[FacetValue]): Unit = {
    logger.info(
      msg
        .flatMap(_.properties)
        .map(_.spaces2)
        .getOrElse("null")
    )
  }

  private def logByteMessage(msg: ConsumerRecord[Array[Byte], Array[Byte]]): Unit = {
    logger.info(
      s"Got ${msg.value().length} bytes"
    )
  }

  private def deserialize(rec: ConsumerRecord[Array[Byte], Array[Byte]]): Option[FacetValue] = {
    val input = AvroInputStream.binary[FacetValue]
    for {
      bytes <- Option(rec.value())
      src    = input.from(bytes)
      res   <- src.build.iterator.toSeq.headOption

    } yield res
  }
}
