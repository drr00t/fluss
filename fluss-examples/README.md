<!--
 Licensed to the Apache Software Foundation (ASF) under one
 or more contributor license agreements.  See the NOTICE file
 distributed with this work for additional information
 regarding copyright ownership.  The ASF licenses this file
 to you under the Apache License, Version 2.0 (the
 "License"); you may not use this file except in compliance
 with the License.  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
-->

Fluss is designed to address the demands of real-time analytics with the following key capabilities:

- Real-Time Stream Reading and Writing: Supports millisecond-level end-to-end latency.
- Columnar Stream: Optimizes storage and query efficiency.
- Streaming Updates: Enables low-latency updates to data streams.
- Changelog Generation: Supports changelog generation and consumption.
- Real-Time Lookup Queries: Facilitates instant lookup queries on primary keys.
- Streaming & Lakehouse Unification: Seamlessly integrates streaming and lakehouse storage for unified data processing.


# Fluss Examples

This is a curated list of examples that showcase event streaming using Apache Pulsar.

# Prerequisites

- Docker + Compose installed on your machine.

```bash
# cd fluss-examples
docker compose up
```

## Full example list

- [Java Client Admin](clientjava-admin/clientjava-admin.md)
- [Java Client Basic Writing](clientjava-basic-write/clientjava-basic-write.md)
- [Basic Schema](clientjava-basic-schema/clientjava-basic-schema.md)
- [Log Table Example](clientjava-log-table/clientjava-logtable.md)
- [PrimaryKey Table Example](clientjava-primarykey-table/clientjava-primarykey-table.md)
- [Table Bucketing](table-bucketing/table-bucketing.md)
- [Table Partitions](table-partitioning/table-partitioning.md)

### PubSub

## Running the Environment

Execute the docker compose to spin up a Fluss cluster, but create the Fluss network first:

```bash
docker network create fluss-net
docker compose up
```

## Building examples

You can build the examples using Maven. Navigate to the `fluss-examples` directory and run:

```bash
mvn clean package
```

## Runing the Examples

You maybe will need setup arrow environment variables to run the examples:

```bash
JDK_JAVA_OPTIONS="--add-opens=java.base/java.nio=org.apache.arrow.memory.core,ALL-UNNAMED" java 






