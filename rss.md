deploy/samza/bin/run-job.sh --config-factory=org.apache.samza.config.factories.PropertiesConfigFactory --config-path=file://$PWD/deploy/samza/config/rss-feed.properties


deploy/samza/bin/run-job.sh --config-factory=org.apache.samza.config.factories.PropertiesConfigFactory --config-path=file://$PWD/deploy/samza/config/rss-parser.properties


deploy/samza/bin/run-job.sh --config-factory=org.apache.samza.config.factories.PropertiesConfigFactory --config-path=file://$PWD/deploy/samza/config/rss-stats.properties


deploy/kafka/bin/kafka-console-consumer.sh  --zookeeper localhost:2181 --topic rss-raw


deploy/kafka/bin/kafka-console-consumer.sh  --zookeeper localhost:2181 --topic rss-item

deploy/kafka/bin/kafka-console-consumer.sh  --zookeeper localhost:2181 --topic rss-stats