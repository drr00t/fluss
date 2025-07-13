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

package com.alibaba.fluss.examples.ecommerce.boundary.streams.producers;

import com.alibaba.fluss.client.Connection;
import com.alibaba.fluss.client.table.writer.AppendWriter;
import com.alibaba.fluss.examples.ecommerce.boundary.data.OrderFakerDataGenerator;
import com.alibaba.fluss.examples.ecommerce.boundary.persistence.model.MappingTables;
import com.alibaba.fluss.examples.ecommerce.boundary.persistence.model.Tables;
import com.alibaba.fluss.examples.ecommerce.boundary.streams.Operation;
import com.alibaba.fluss.examples.ecommerce.entity.Customer;
import com.alibaba.fluss.examples.ecommerce.entity.Product;
import com.alibaba.fluss.row.GenericRow;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * This class serves as a Data Access Object (DAO) for the e-commerce application. It is responsible
 * for interacting with the database to perform CRUD operations on e-commerce entities such as
 * customers, orders, products, and sales.
 */
public class OrderProducer extends Operation {
    private static final Logger logger = LoggerFactory.getLogger(OrderProducer.class);
    private final AppendWriter appendWriter;

    public OrderProducer(Connection storageConnection) {
        super(storageConnection, Tables.ORDER_TABLE_PATH);
        appendWriter = produceToTable.newAppend().createWriter();
    }

    public static OrderProducer setupWith(Connection connection) {

        logger.info("Initializing producer with connection: {}", connection);

        return new OrderProducer(connection);
    }

    public void produceOrderData(
            List<Customer> customers, List<Product> products, int productToProduce) {
        OrderFakerDataGenerator dataGenerator = new OrderFakerDataGenerator(customers, products);
        dataGenerator
                .generateMany(productToProduce)
                .forEach(
                        order -> {
                            GenericRow row = MappingTables.ofOrder(order);
                            appendWriter.append(row);
                        });

        appendWriter.flush();

        logger.info("Order data Successfully written.");
    }
}
