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
package samza.examples.rss.task;

import org.apache.log4j.Logger;
import org.apache.samza.config.Config;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.task.*;

/**
 * This class designed for the test purpose, when you test the task, please implement the testXXX method in your test task.
 * Created by J28848 on 10/4/16.
 */
public abstract class BaseStreamTask implements InitableTask,ClosableTask, StreamTask {

    public Logger logger = Logger.getLogger(BaseStreamTask.class);

    @Override
    public void close() throws Exception {
    }

    @Override
    public void init(Config config, TaskContext context) throws Exception {
        testInit(config,context);
    }
    @Override
    public void process(IncomingMessageEnvelope incomingMessageEnvelope, MessageCollector messageCollector, TaskCoordinator taskCoordinator) throws Exception {
        doProcess(incomingMessageEnvelope, messageCollector, taskCoordinator);
        testProcess(incomingMessageEnvelope,messageCollector,taskCoordinator);
    }
    public abstract void doProcess(IncomingMessageEnvelope incomingMessageEnvelope, MessageCollector messageCollector, TaskCoordinator taskCoordinator) throws Exception;

    /**
     * assume you used the method for the test.
     * @param incomingMessageEnvelope
     * @param messageCollector
     * @param taskCoordinator
     * @throws Exception
     */
    public void testProcess(IncomingMessageEnvelope incomingMessageEnvelope, MessageCollector messageCollector, TaskCoordinator taskCoordinator) throws Exception{
        //TODO
    }
    public void testInit(Config config, TaskContext context) throws Exception {

    }
}
