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

package com.alibaba.fluss.examples.ecommerce.boundary.data;

import com.alibaba.fluss.examples.ecommerce.control.datageneration.DataGenerator;
import com.alibaba.fluss.examples.ecommerce.entity.Order;
import com.alibaba.fluss.examples.ecommerce.entity.Sale;

import net.datafaker.Faker;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

/**
 * Repository interface for managing Customer entities. This interface extends the generic
 * Repository interface.
 */
public class SaleFakerDataGenerator implements DataGenerator<Sale> {
    private final List<Order> orders;

    public SaleFakerDataGenerator(List<Order> orders) {
        this.orders = orders;
    }

    @Override
    public Sale generateOne() {
        return generateMany(1).stream().findFirst().orElse(null);
    }

    @Override
    public List<Sale> generateMany(int count) {
        Order order = orders.get(faker.number().numberBetween(0, orders.size() - 1));
        return faker.collection(
                        () -> {
                            LocalDateTime orderDate =
                                    order.orderDate().plusDays(new Random().nextInt(30));

                            return Sale.create(
                                    order.id(),
                                    order.productId(),
                                    order.quantity(),
                                    orderDate,
                                    order.amount());
                        })
                .maxLen(count)
                .generate();
    }

    private final Faker faker = new Faker();
}
