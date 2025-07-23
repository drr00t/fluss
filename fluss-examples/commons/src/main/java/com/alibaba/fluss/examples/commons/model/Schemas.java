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

package com.alibaba.fluss.examples.commons.model;

import com.alibaba.fluss.metadata.Schema;
import com.alibaba.fluss.types.DataTypes;
import com.alibaba.fluss.types.DecimalType;

/**
 * This class defines the schemas for various entities in the e-commerce application, including
 * customers, products, orders, and sales. Each schema is defined using the Fluss Schema API.
 */
public class Schemas {
    private static final DecimalType MONEY_TYPE = DataTypes.DECIMAL(9, 6);
    public static final Schema CUSTOMER_SCHEMA =
            Schema.newBuilder()
                    .column("id", DataTypes.BIGINT())
                    .column("name", DataTypes.STRING())
                    .column("birthdate", DataTypes.DATE())
                    .column("address", DataTypes.STRING())
                    .column("city", DataTypes.STRING())
                    .column("createdAt", DataTypes.TIMESTAMP())
                    .column("updatedAt", DataTypes.TIMESTAMP())
                    .primaryKey("id")
                    .build();

    public static final Schema PRODUCT_SCHEMA =
            Schema.newBuilder()
                    .column("id", DataTypes.BIGINT())
                    .column("name", DataTypes.STRING())
                    .column("description", DataTypes.STRING())
                    .column("price", MONEY_TYPE)
                    .column("category", DataTypes.STRING())
                    .primaryKey("id")
                    .build();

    public static final Schema ORDER_SCHEMA =
            Schema.newBuilder()
                    .column("id", DataTypes.BIGINT())
                    .column("customerId", DataTypes.BIGINT())
                    .column("productId", DataTypes.BIGINT())
                    .column("orderDate", DataTypes.TIMESTAMP())
                    .column("quantity", DataTypes.BIGINT())
                    .column("amount", MONEY_TYPE)
                    .build();

    public static final Schema SALE_SCHEMA =
            Schema.newBuilder()
                    .column("id", DataTypes.BIGINT())
                    .column("orderId", DataTypes.BIGINT())
                    .column("productId", DataTypes.BIGINT())
                    .column("price", MONEY_TYPE)
                    .column("saleDate", DataTypes.TIMESTAMP())
                    .build();
}
