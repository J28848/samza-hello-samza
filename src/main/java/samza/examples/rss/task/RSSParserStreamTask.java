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

import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.system.OutgoingMessageEnvelope;
import org.apache.samza.system.SystemStream;
import org.apache.samza.task.MessageCollector;
import org.apache.samza.task.StreamTask;
import org.apache.samza.task.TaskCoordinator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by J28848 on 7/24/16.
 */
public class RSSParserStreamTask implements StreamTask{
    private final static SystemStream OUTPUT_STREAM = new SystemStream("kafka", "rss-item");
    @Override
    public void process(IncomingMessageEnvelope incomingMessageEnvelope, MessageCollector messageCollector, TaskCoordinator taskCoordinator) throws Exception {
        String content = (String)incomingMessageEnvelope.getMessage();
        try{
            List<Map<String, String>> obj = parser(content);
            messageCollector.send(new OutgoingMessageEnvelope(OUTPUT_STREAM,obj));
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
    private List<Map<String, String>> parser(String content){
        List<Map<String, String>> res = new ArrayList<Map<String, String>>();
        for (int i = 0; i < 5; i++) {
            Map<String,String> m = new HashMap<String,String>();
            m.put("title","t"+i);
            m.put("source","s"+i);
            res.add(m);
        }

        return res;
    }
}
