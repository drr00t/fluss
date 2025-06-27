package com.alibaba.fluss;

import com.alibaba.fluss.client.Connection;
import com.alibaba.fluss.client.ConnectionFactory;
import com.alibaba.fluss.client.admin.Admin;
import com.alibaba.fluss.client.table.Table;
import com.alibaba.fluss.config.Configuration;
import com.alibaba.fluss.metadata.TablePath;

// TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        // creating Connection object to connect with Fluss cluster
        Configuration conf = new Configuration();
        conf.setString("bootstrap.servers", "localhost:9123");
        Connection connection = ConnectionFactory.createConnection(conf);


// obtain Admin instance from the Connection
        Admin admin = connection.getAdmin();
        admin.listDatabases().get().forEach(System.out::println);

// obtain Table instance from the Connection
        Table table = connection.getTable(TablePath.of("my_db", "my_table");
        System.out.println(table.getTableInfo());
    }
}
