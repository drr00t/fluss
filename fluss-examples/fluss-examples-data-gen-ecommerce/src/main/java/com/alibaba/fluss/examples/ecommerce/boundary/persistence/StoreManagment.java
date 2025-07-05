/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.alibaba.fluss.examples.ecommerce.boundary.persistence;

import com.alibaba.fluss.client.Connection;
import com.alibaba.fluss.client.admin.Admin;
import com.alibaba.fluss.client.table.Table;
import com.alibaba.fluss.client.table.writer.AppendWriter;
import com.alibaba.fluss.examples.ecommerce.boundary.datagenerator.CustomerFakerGenerator;
import com.alibaba.fluss.examples.ecommerce.boundary.persistence.model.TableMappings;
import com.alibaba.fluss.metadata.DatabaseDescriptor;
import com.alibaba.fluss.metadata.DatabaseInfo;
import com.alibaba.fluss.metadata.TableDescriptor;
import com.alibaba.fluss.metadata.TablePath;
import com.alibaba.fluss.row.GenericRow;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;

/**
 * This class serves as a Data Access Object (DAO) for the e-commerce application. It is responsible
 * for interacting with the database to perform CRUD operations on e-commerce entities such as
 * customers, orders, products, and sales.
 */
public class StoreManagment {
    private static final Logger logger = LoggerFactory.getLogger(StoreManagment.class);
    private final Admin admin;
    private final Connection storageConnection;

    public StoreManagment(Connection storageConnection) {
        this.admin = storageConnection.getAdmin();
        this.storageConnection = storageConnection;
    }

    public static StoreManagment setupWith(Connection connection) {
        logger.info("Initializing StoreManagment with connection: {}", connection);
        return new StoreManagment(connection);
    }

    public void setupDatabase(String dbName, DatabaseDescriptor descriptor)
            throws ExecutionException, InterruptedException {

        if (admin.databaseExists(dbName).get()) {
            logger.info("Database {} already exists, skipping creation.", dbName);
            return;
        }

        admin.createDatabase(dbName, descriptor, false).get();
        logger.info("Database {} created successfully with descriptor: {}", dbName, descriptor);

        DatabaseInfo dbInfo = admin.getDatabaseInfo(dbName).get();
        logger.info("Database info: {}", new String(dbInfo.getDatabaseDescriptor().toJsonBytes()));
    }

    public void setupTable(TableDescriptor descriptor, TablePath tablePath)
            throws ExecutionException, InterruptedException {
        logger.info("Setting up table: {} with descriptor: {}", tablePath, descriptor);

        if (admin.tableExists(tablePath).get()) {
            logger.info("Table {} already exists, skipping creation.", tablePath);
            //            admin.dropTable(Tables.CUSTOMER_TABLE_PATH, true).get();
            return;
        }

        admin.createTable(tablePath, descriptor, true).get();
        logger.info("Table {} created successfully.", tablePath);

        if (!admin.tableExists(tablePath).get()) {
            logger.error("Failed to create table: {}", tablePath);
            return;
        }

        Table table = storageConnection.getTable(tablePath);
        logger.info("Table {} is ready for use.", tablePath);
    }

    public void writingCustomerData(TablePath pathPath, int customerCount) {

        Table table = storageConnection.getTable(pathPath);
        AppendWriter writer = table.newAppend().createWriter();

        CustomerFakerGenerator dataGenerator = new CustomerFakerGenerator();
        dataGenerator
                .generateMany(customerCount)
                .forEach(
                        reading -> {
                            GenericRow row = TableMappings.ofCustomer(reading);
                            writer.append(row);
                        });
        writer.flush();

        logger.info("Customer data Written Successfully.");

        //        logger.info("Creating table writer for table {} ...",
        // AppUtils.SENSOR_INFORMATION_TBL);
        //        Table sensorInfoTable = connection.getTable(AppUtils.getSensorInfoTablePath());
        //        UpsertWriter upsertWriter = sensorInfoTable.newUpsert().createWriter();
        //
        //        AppUtils.sensorInfos.forEach(
        //                sensorInfo -> {
        //                    GenericRow row = sensorInfoToRow(sensorInfo);
        //                    upsertWriter.upsert(row);
        //                });
        //        upsertWriter.flush();

        logger.info("Sensor Information Successfully.");

        logger.info("Closing writers and connections.");

        try {
            admin.close();
            storageConnection.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
