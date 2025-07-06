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

package com.alibaba.fluss.examples.ecommerce.boundary;

import com.alibaba.fluss.client.Connection;
import com.alibaba.fluss.examples.ecommerce.boundary.persistence.StoreManagment;
import com.alibaba.fluss.examples.ecommerce.boundary.persistence.model.EcommerceDatabase;
import com.alibaba.fluss.examples.ecommerce.boundary.persistence.model.Tables;

import org.slf4j.Logger;

import java.util.concurrent.ExecutionException;

/**
 * This class serves as the entry point for the E-commerce Data Generator application. It
 * initializes the application and sets up the necessary components for generating e-commerce data.
 */
public class EcommerceDataGenerator {
    private static Logger logger = org.slf4j.LoggerFactory.getLogger(EcommerceDataGenerator.class);

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        logger.info("E-commerce Data Generator is starting...");
        String bootstrapServers = "localhost:9123"; // Example bootstrap server
        Connection connection = EcommerceDatabase.getConnection(bootstrapServers);

        StoreManagment management = StoreManagment.setupWith(connection);

        management.setupDatabase(
                EcommerceDatabase.DB_NAME, EcommerceDatabase.ECOMMERCE_DB_DESCRIPTOR);

        management.setupTable(
                EcommerceDatabase.CUSTOMER_TABLE_DESCRIPTOR, Tables.CUSTOMER_TABLE_PATH);

        logger.info("E-commerce Data Generator setup completed successfully.");

        int customersToGenerate = 10000; // Example customer count
        logger.info("Let's Generate {} Customers.", customersToGenerate);
        management.writingCustomerData(Tables.CUSTOMER_TABLE_PATH, 10000);
        logger.info("{} Customers written to database.", customersToGenerate);

        logger.info("Start to reading customer data from database.");

        management.readingCustomerData(Tables.CUSTOMER_TABLE_PATH);
    }
}
