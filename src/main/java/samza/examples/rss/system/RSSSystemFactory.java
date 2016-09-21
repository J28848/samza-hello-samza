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
package samza.examples.rss.system;

import org.apache.samza.SamzaException;
import org.apache.samza.config.Config;
import org.apache.samza.metrics.MetricsRegistry;
import org.apache.samza.system.SystemAdmin;
import org.apache.samza.system.SystemConsumer;
import org.apache.samza.system.SystemFactory;
import org.apache.samza.system.SystemProducer;
import org.apache.samza.util.SinglePartitionWithoutOffsetsSystemAdmin;

/**
 * Created by J28848 on 7/24/16.
 */
public class RSSSystemFactory implements SystemFactory{
    @Override
    public SystemConsumer getConsumer(String systemName, Config config, MetricsRegistry metricsRegistry) {
        String url = config.get("systems." + systemName + ".url");
        String keyword = config.get("systems." + systemName + ".keyword");
        RSSFeed feed = new RSSFeed(url, keyword);
        return new RSSConsumer(systemName, feed);
    }

    @Override
    public SystemProducer getProducer(String s, Config config, MetricsRegistry metricsRegistry) {
        throw new SamzaException("You can't get a produce for rss feed!");
    }

    @Override
    public SystemAdmin getAdmin(String s, Config config) {
        return new SinglePartitionWithoutOffsetsSystemAdmin();
    }
}
