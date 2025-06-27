package com.alibaba.fluss;

import com.alibaba.fluss.client.Connection;
import com.alibaba.fluss.client.ConnectionFactory;
import com.alibaba.fluss.client.admin.Admin;
import com.alibaba.fluss.client.table.Table;
import com.alibaba.fluss.client.table.scanner.ScanRecord;
import com.alibaba.fluss.client.table.scanner.log.LogScanner;
import com.alibaba.fluss.client.table.scanner.log.ScanRecords;
import com.alibaba.fluss.client.table.writer.UpsertWriter;
import com.alibaba.fluss.config.Configuration;
import com.alibaba.fluss.metadata.*;
import com.alibaba.fluss.row.BinaryString;
import com.alibaba.fluss.row.GenericRow;
import com.alibaba.fluss.row.InternalRow;
import com.alibaba.fluss.row.TimestampNtz;
import com.alibaba.fluss.types.DataTypes;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

// TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class MainFlussClientStreaming {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        // creating Connection object to connect with Fluss cluster
        Configuration conf = new Configuration();
        conf.setString("bootstrap.servers", "localhost:9123");
        Connection connection = ConnectionFactory.createConnection(conf);

        class User{
            private String id;
            private int age;
            private LocalDateTime createdAt;
            private boolean isActive;

            public User(String id, int age, LocalDateTime createdAt, boolean isActive) {
                this.id = id;
                this.age = age;
                this.createdAt = createdAt;
                this.isActive = isActive;
            }

            public String getId() {
                return id;
            }

            public int getAge() {
                return age;
            }

            public LocalDateTime getCreatedAt() {
                return createdAt;
            }

            public boolean isActive() {
                return isActive;
            }
        }

        // Create database descriptor
        DatabaseDescriptor databaseDescriptor = DatabaseDescriptor.builder()
                .comment("This is a test database")
                .customProperty("owner", "data-team")
                .build();

        Schema schema = Schema.newBuilder()
                .column("id", DataTypes.STRING())
                .column("age", DataTypes.INT())
                .column("created_at", DataTypes.TIMESTAMP())
                .column("is_active", DataTypes.BOOLEAN())
                .primaryKey("id")
                .build();

        // Use the schema in a table descriptor
        TableDescriptor tableDescriptor = TableDescriptor.builder()
                .schema(schema)
                .distributedBy(1, "id")  // Distribute by the id column with 1 buckets
                .build();

        try {
            // obtain Admin instance from the Connection
            Admin admin = connection.getAdmin();

            // Create database (true means ignore if exists)
            admin.createDatabase("my_db", databaseDescriptor, true).get();

            TablePath tablePath = TablePath.of("my_db", "user_table");

            admin.createTable(tablePath, tableDescriptor, true).get();
            System.out.println("Table created successfully");

            // obtain Table instance from the Connection
            Table table = connection.getTable(tablePath);
            System.out.println(table.getTableInfo());

            table.newUpsert().createWriter();

            List<User> users = List.of(
                    new User("1", 20, LocalDateTime.now() , true),
                    new User("2", 22, LocalDateTime.now() , true),
                    new User("3", 23, LocalDateTime.now() , true),
                    new User("4", 24, LocalDateTime.now() , true),
                    new User("5", 25, LocalDateTime.now() , true)
            );


            List<GenericRow> rows = users.stream().map(user -> {
                GenericRow row = new GenericRow(4);
                row.setField(0, BinaryString.fromString(user.getId()));
                row.setField(1, user.getAge());
                row.setField(2, TimestampNtz.fromLocalDateTime(user.getCreatedAt()));
                row.setField(3, user.isActive());
                return row;
            }).collect(Collectors.toList());

            System.out.println("Upserting rows to the table");
            UpsertWriter writer = table.newUpsert().createWriter();

            // upsert() is a non-blocking call that sends data to Fluss server with batching and timeout
            rows.forEach(writer::upsert);

            // call flush() to blocking the thread until all data is written successfully
            writer.flush();

            LogScanner logScanner = table.newScan().createLogScanner();

            int numBuckets = table.getTableInfo().getNumBuckets();
            System.out.println("Number of buckets: " + numBuckets);

            for (int i = 0; i < numBuckets; i++) {
                System.out.println("Subscribing to bucket " + i);
                logScanner.subscribeFromBeginning(i);
            }

            long scanned = 0;
            Map<Integer, List<String>> rowsMap = new HashMap<>();

            while (true) {
                System.out.println("Polling for records...");
                ScanRecords scanRecords = logScanner.poll(Duration.ofSeconds(1));
                for (TableBucket bucket : scanRecords.buckets()) {
                    for (ScanRecord record : scanRecords.records(bucket)) {
                        InternalRow row = record.getRow();
                        System.out.println("Subscribing to bucket " + row.toString());
                    }
                }
                scanned += scanRecords.count();
            }

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}
