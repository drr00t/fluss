/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.alibaba.fluss.lake.iceberg;

import com.alibaba.fluss.config.Configuration;
import com.alibaba.fluss.lake.lakestorage.LakeCatalog;
import com.alibaba.fluss.lake.lakestorage.LakeStorage;
import com.alibaba.fluss.lake.writer.LakeTieringFactory;

/** Iceberg implementation of {@link LakeStorage}. */
public class IcebergLakeStorage implements LakeStorage {

    private final Configuration icebergConfig;

    public IcebergLakeStorage(Configuration configuration) {
        this.icebergConfig = configuration;
    }

    @Override
    public LakeTieringFactory<?, ?> createLakeTieringFactory() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public LakeCatalog createLakeCatalog() {
        throw new UnsupportedOperationException("Not implemented");
    }
}
