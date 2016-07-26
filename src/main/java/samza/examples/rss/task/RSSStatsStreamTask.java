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

import org.apache.samza.config.Config;
import org.apache.samza.storage.kv.KeyValueStore;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.system.OutgoingMessageEnvelope;
import org.apache.samza.system.SystemStream;
import org.apache.samza.task.*;

import java.util.*;


public class RSSStatsStreamTask implements StreamTask, InitableTask, WindowableTask {
    private int edits = 0;
    private int byteDiff = 0;
    private Set<String> titles = new HashSet<String>();
    private Map<String, Integer> counts = new HashMap<String, Integer>();
    private KeyValueStore<String, Integer> store;

    public void init(Config config, TaskContext context) {
        this.store = (KeyValueStore<String, Integer>) context.getStore("rss-stats");
    }

    @SuppressWarnings("unchecked")
    @Override
    public void process(IncomingMessageEnvelope envelope, MessageCollector collector, TaskCoordinator coordinator) {
        List<Map<String, String>> edit = (List<Map<String, String>>) envelope.getMessage();
        for(Map<String,String> m : edit){
            Integer editsAllTime = store.get("count-edits-all-time");
            if (editsAllTime == null) editsAllTime = 0;
            store.put("count-edits-all-time", editsAllTime + 1);

            edits += 1;
            titles.add((String) m.get("title"));

//            for (Map.Entry<String, Boolean> flag : flags.entrySet()) {
//                if (Boolean.TRUE.equals(flag.getValue())) {
//                    Integer count = counts.get(flag.getKey());
//
//                    if (count == null) {
//                        count = 0;
//                    }
//
//                    count += 1;
//                    counts.put(flag.getKey(), count);
//                }
//            }
        }

    }

    @Override
    public void window(MessageCollector collector, TaskCoordinator coordinator) {
        counts.put("edits", edits);
        counts.put("bytes-added", byteDiff);
        counts.put("unique-titles", titles.size());
        counts.put("edits-all-time", store.get("count-edits-all-time"));

        collector.send(new OutgoingMessageEnvelope(new SystemStream("kafka", "rss-stats"), counts));

        // Reset counts after windowing.
        edits = 0;
        byteDiff = 0;
        titles = new HashSet<String>();
        counts = new HashMap<String, Integer>();
    }
}
