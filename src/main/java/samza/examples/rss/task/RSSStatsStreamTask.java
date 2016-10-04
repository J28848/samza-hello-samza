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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.samza.config.Config;
import org.apache.samza.storage.kv.KeyValueStore;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.system.OutgoingMessageEnvelope;
import org.apache.samza.system.SystemStream;
import org.apache.samza.task.*;

import java.util.*;


public class RSSStatsStreamTask extends BaseStreamTask implements WindowableTask {
    private int edits = 0;
    private int byteDiff = 0;
    private Set<String> titles = new HashSet<String>();
    private Map<String, Integer> counts = new HashMap<String, Integer>();
    private KeyValueStore<String, Integer> store;
    private final Gson gson = new Gson();
    public void init(Config config, TaskContext context) {
        this.store = (KeyValueStore<String, Integer>) context.getStore("rss-stats");
    }

    @SuppressWarnings("unchecked")
    @Override
    public void doProcess(IncomingMessageEnvelope envelope, MessageCollector collector, TaskCoordinator coordinator) {
        String message = (String)envelope.getMessage();
        List<Map<String, String>> edit = gson.fromJson(message, new TypeToken<List<Map<String, String>>>(){}.getType());
        for(Map<String,String> m : edit){
            Integer editsAllTime = store.get("count-edits-all-time");
            if (editsAllTime == null) editsAllTime = 0;
            store.put("count-edits-all-time", editsAllTime + 1);

            edits += 1;
            titles.add((String) m.get("title"));
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
