import akka.stream.scaladsl.Source
import akka.stream.OverflowStrategy
import akka.stream.scaladsl.Keep
import akka.actor.ActorSystem
import akka.stream.QueueOfferResult
import akka.stream.testkit.scaladsl.TestSink
import scala.concurrent.Await
import scala.concurrent.duration._
import akka.stream.Attributes

class SourceQueueDropNewTest extends munit.FunSuite {

  implicit val system = ActorSystem("test")

  val awaitTimeout     = 5.seconds
  val queueBufferSize  = 1
  val concurrentOffers = 1

  test("source queue with dropNew then recover") {
    val (queue, probe) = Source
      .queue[Int](queueBufferSize, OverflowStrategy.dropNew, concurrentOffers)
      .log("queue")
      .addAttributes(
        Attributes.logLevels(
          onElement = Attributes.LogLevels.Info,
          onFinish = Attributes.LogLevels.Info,
          onFailure = Attributes.LogLevels.Error
        )
      )
      // .groupBy(1, _ => 0)
      // .mergeSubstreams
      .toMat(TestSink.probe[Int])(Keep.both)
      .run()

    val firstOffer  = queue.offer(1)
    val secondOffer = queue.offer(2)
    val thirdOffer  = queue.offer(3)

    probe.request(1)

    // Allowed as the stream has requested an element
    assertEquals(Await.result(firstOffer, awaitTimeout), QueueOfferResult.Enqueued)
    // Next one allowed because of concurrent offers
    assertEquals(Await.result(secondOffer, awaitTimeout), QueueOfferResult.Enqueued)
    // Not allowed because buffer is full and concurrent offers reached max
    assertEquals(Await.result(thirdOffer, awaitTimeout), QueueOfferResult.Dropped)
    // Demand more elements and see that subsequent offers are ok
    probe.request(1)
    val fourthOffer = queue.offer(4)
    assertEquals(Await.result(fourthOffer, awaitTimeout), QueueOfferResult.Enqueued)
  }
}
