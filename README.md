hello-samza
===========

Hello Samza is a starter project for [Apache Samza](http://samza.apache.org/) jobs.

Please see [Hello Samza](http://samza.apache.org/startup/hello-samza/0.9/) to get started.

### Pull requests and questions

[Hello Samza](http://samza.apache.org/startup/hello-samza/0.10/) is developed as part of the [Apache Samza](http://samza.apache.org) project. Please direct questions, improvements and bug fixes there. Questions about [Hello Samza](http://samza.apache.org/startup/hello-samza/0.9/) are welcome on the [dev list](http://samza.apache.org/community/mailing-lists.html) and the [Samza JIRA](https://issues.apache.org/jira/browse/SAMZA) has a hello-samza component for filing tickets.

### Contribution

To start contributing on [Hello Samza](http://samza.apache.org/startup/hello-samza/0.10/) first read [Rules](http://samza.apache.org/contribute/rules.html) and [Contributor Corner](https://cwiki.apache.org/confluence/display/SAMZA/Contributor%27s+Corner). Notice that [Hello Samza](http://samza.apache.org/startup/hello-samza/0.10/) git repository does not support git pull request.

rss-example
===========

# Before Deployment

1. make suer the host file configuration, it's better use the external IP or hostname
2. make sure JDK version 1.7+

# Deployment step
## master server

**assume kafka+yarn+zookeeper run in the same server**

>clone source code.

```
git clone https://github.com/J28848/samza-hello-samza
```
>switch branch to `features/rss-example`

```
git checkout features/rss-example
```

>install the service

bin/grid install master

```
mvn clean package -Dzkserver=${yourip} -Dkafkaserver=${yourip} -Dhttpserver=${yourip}
mkdir -p deploy/samza
tar -xvf ./target/hello-samza-0.10.1-dist.tar.gz -C deploy/samza
```

>change the default kafka server ip

 ```
vi deploy/kafka/config/server.properties
### your external ip/hostname
advertised.host.name=${yourip}

vi deploy/kafka/config/server.properties
metadata.broker.list=${yourip}:port
 ```

>change yarn resourcenamer.hostname and nodemanager.hostname

```
vi deploy/yarn/etc/hadoop/yarn-site.xml
  <property>
    <name>yarn.resourcemanager.hostname</name>
    <value>0.0.0.0</value>
  </property>
  <property>
    <name>yarn.nodemanager.hostname</name>
    <value>${yourip}</value>
  </property>
```

>start the service and start the simple httpserver with `python`

```
bin/grid start all
bin/grid start pserver
```

## node server

1. ssh to another server as node server

>also need clone a source code from github and checkout to the `features/rss-example` branch

2. install the service
```
 bin/grid install node
```

3. change the yarn-site.xml config file.

```
vi deploy/yarn/etc/hadoop/yarn-site.xml
  <property>
    <name>yarn.resourcemanager.hostname</name>
    <value>${yourmasteripd}</value>
  </property>
  <property>
    <name>yarn.nodemanager.hostname</name>
    <value>${yourip}</value>
  </property>
```

4. start the service.
```
bin/grid start nodemanager
```


## deploy task
>excute following task in **master** server.

```
deploy/samza/bin/run-job.sh --config-factory=org.apache.samza.config.factories.PropertiesConfigFactory --config-path=file://$PWD/deploy/samza/config/rss-feed.properties


deploy/samza/bin/run-job.sh --config-factory=org.apache.samza.config.factories.PropertiesConfigFactory --config-path=file://$PWD/deploy/samza/config/rss-parser.properties


deploy/samza/bin/run-job.sh --config-factory=org.apache.samza.config.factories.PropertiesConfigFactory --config-path=file://$PWD/deploy/samza/config/rss-stats.properties

```

>check the result with following shell

```
deploy/kafka/bin/kafka-console-consumer.sh  --zookeeper localhost:2181 --topic rss-stats
```

>it will be done when you see the result is not ZERO


```
{"bytes-added":0,"edits":0,"edits-all-time":980,"unique-titles":0}
```

>you can also use the following to check each task result

```
deploy/kafka/bin/kafka-console-consumer.sh  --zookeeper localhost:2181 --topic rss-raw

deploy/kafka/bin/kafka-console-consumer.sh  --zookeeper localhost:2181 --topic rss-item
```