/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package samza.examples.rss.task

import java.util.concurrent.{CountDownLatch, TimeUnit}

import org.apache.samza.config.Config
import org.apache.samza.system.IncomingMessageEnvelope
import org.apache.samza.task.{InitableTask, MessageCollector, TaskContext, TaskCoordinator}
import org.junit.Assert._
import org.junit.{AfterClass, BeforeClass, Test}


/**
  * Created by J28848 on 10/4/16.
  */
object MyRSSParserStreamTestTask{
  @BeforeClass
  def beforeSetupServers {
    StreamTaskTestUtil.beforeSetupServers
  }

  @AfterClass
  def afterCleanLogDirs {
    StreamTaskTestUtil.afterCleanLogDirs
  }
}
class MyRSSParserStreamTestTask extends StreamTaskTestUtil{


  StreamTaskTestUtil(Map(
    "job.name" -> "rssparser",
    "task.class" -> "samza.examples.rss.task.MyRSSParserStreamTask",

    "stores.rssparser.factory" -> "org.apache.samza.storage.kv.inmemory.InMemoryKeyValueStorageEngineFactory",
    "stores.rssparser.key.serde" -> "string",
    "stores.rssparser.msg.serde" -> "string",
    "stores.rssparser.changelog" -> "kafka.rssparserChangelog",
    "stores.rssparser.changelog.replication.factor" -> "1",
    "systems.kafka.samza.factory" -> "org.apache.samza.system.kafka.KafkaSystemFactory",
    // However, don't have the inputs use the checkpoint manager
    // since the second part of the test expects to replay the input streams.
    "systems.kafka.streams.input.samza.reset.offset" -> "true"))
  @Test
  def testRssParserTask {
    // Have to do this in one test to guarantee ordering.
    testShouldStartTaskForFirstTime
    //    testShouldRestoreStore
  }

  def testShouldStartTaskForFirstTime {
    val (job, task) = startJob
    // Validate that restored is empty.
    /* assertEquals(0, MyTestTask.initFinished.getCount)
     assertEquals(true, MyTestTask.asInstanceOf[MyTopAuthorStreamTask])
     assertEquals(0, MyTestTask.received.size)*/

    // Send some messages to input stream.
    val dataStr = "<!DOCTYPE html> <html> <head> <title>test</title> </head> <body> <div> <h1>This is title</h1> <dir> <p>This is content</p> </dir> </div> </body> </html>";
    //topSimilarDoc
    send(dataStr)
    //    Validate that messages appear in store stream.
    val messages = readAll("rss-item", 0, "testRssItem")

    assertEquals(1, messages.length)

    stopJob(job)
  }

}
class MyRSSParserStreamTask extends RSSParserStreamTask with InitableTask {
  var received = MyTestTask.received
  val initFinished = MyTestTask.initFinished
  var gotMessage = MyTestTask.gotMessage

  override def testInit(config: Config, context: TaskContext) {
    MyTestTask.register(context.getTaskName, this)
    initFinished.countDown()
  }



  def awaitMessage {
    assertTrue("Timed out of waiting for message rather than received one.", gotMessage.await(60, TimeUnit.SECONDS))
    assertEquals(0, gotMessage.getCount)
    gotMessage = new CountDownLatch(1)
  }
  override def testProcess(envelope: IncomingMessageEnvelope, collector: MessageCollector, coordinator: TaskCoordinator): Unit ={
    val msg = envelope.getMessage.asInstanceOf[String]
    System.err.println("TestTask.process() : %s" format msg)
    received += msg
    gotMessage.countDown
  }
}
