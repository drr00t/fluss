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

package com.alibaba.fluss.examples.ecommerce.boundary.model;

import com.alibaba.fluss.examples.ecommerce.entity.Customer;

import net.datafaker.Faker;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

class FakerRepositoryTest {

    @Test
    void createCustomer() {
        Faker faker = new Faker();
        Customer customer =
                Customer.create(
                        faker.name().fullName(),
                        faker.address().fullAddress(),
                        LocalDate.now().minusYears(20),
                        faker.address().city());

        Customer customer1 =
                Customer.create(
                        faker.name().fullName(),
                        faker.address().fullAddress(),
                        LocalDate.now().minusYears(20),
                        faker.address().city());
        Customer customer2 =
                Customer.create(
                        faker.name().fullName(),
                        faker.address().fullAddress(),
                        LocalDate.now().minusYears(20),
                        faker.address().city());

        // Add assertions to verify the properties of the created customer
        Assertions.assertNotNull(customer.name());
        Assertions.assertNotNull(customer.address());
        Assertions.assertNotNull(customer.birthdate());
        Assertions.assertNotNull(customer.city());

        System.out.println(customer.toString());
        System.out.println(customer1.toString());
        System.out.println(customer2.toString());
    }

    @Test
    @DisplayName("Generate Fake Customer")
    void generateFakeCustomer() {
        FakerRepository respotiry = new FakerRepository();

        Customer customer = respotiry.generateOne();

        // Add assertions to verify the properties of the generated customer
        Assertions.assertNotNull(customer.id());
        Assertions.assertNotNull(customer.name());
        Assertions.assertNotNull(customer.address());
        Assertions.assertNotNull(customer.birthdate());
        Assertions.assertNotNull(customer.city());

        System.out.print(customer.toString());
    }

    @Test
    void generateManyCustomers() {
        FakerRepository respotiry = new FakerRepository();

        int count = 1000;
        List<Customer> customers = respotiry.generateMany(count);

        // Add assertions to verify the properties of the generated customers
        Assertions.assertEquals(count, customers.size());
        for (Customer customer : customers) {
            Assertions.assertNotNull(customer.id());
            Assertions.assertNotNull(customer.name());
            Assertions.assertNotNull(customer.address());
            Assertions.assertNotNull(customer.birthdate());
            Assertions.assertNotNull(customer.city());
        }
    }
}
