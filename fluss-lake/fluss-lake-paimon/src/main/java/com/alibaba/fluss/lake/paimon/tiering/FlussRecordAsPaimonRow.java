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

package com.alibaba.fluss.lake.paimon.tiering;

import com.alibaba.fluss.record.LogRecord;
import com.alibaba.fluss.row.TimestampLtz;
import com.alibaba.fluss.row.TimestampNtz;

import org.apache.paimon.data.BinaryString;
import org.apache.paimon.data.Decimal;
import org.apache.paimon.data.InternalArray;
import org.apache.paimon.data.InternalMap;
import org.apache.paimon.data.InternalRow;
import org.apache.paimon.data.Timestamp;
import org.apache.paimon.data.variant.Variant;
import org.apache.paimon.types.DataType;
import org.apache.paimon.types.RowKind;
import org.apache.paimon.types.RowType;

import static com.alibaba.fluss.lake.paimon.utils.PaimonConversions.toRowKind;
import static com.alibaba.fluss.utils.Preconditions.checkState;

/** To wrap Fluss {@link LogRecord} as paimon {@link InternalRow}. */
public class FlussRecordAsPaimonRow implements InternalRow {

    // Lake table for paimon will append three system columns: __bucket, __offset,__timestamp
    private static final int LAKE_PAIMON_SYSTEM_COLUMNS = 3;
    private final RowType tableTowType;
    private final int bucket;
    private LogRecord logRecord;
    private int originRowFieldCount;
    private com.alibaba.fluss.row.InternalRow internalRow;

    public FlussRecordAsPaimonRow(int bucket, RowType tableTowType) {
        this.bucket = bucket;
        this.tableTowType = tableTowType;
    }

    public void setFlussRecord(LogRecord logRecord) {
        this.logRecord = logRecord;
        this.internalRow = logRecord.getRow();
        this.originRowFieldCount = internalRow.getFieldCount();
        checkState(
                originRowFieldCount == tableTowType.getFieldCount() - LAKE_PAIMON_SYSTEM_COLUMNS,
                "The paimon table fields count must equals to LogRecord's fields count.");
    }

    @Override
    public int getFieldCount() {
        return
        //  business (including partitions) + system (three system fields: bucket, offset,
        // timestamp)
        originRowFieldCount + LAKE_PAIMON_SYSTEM_COLUMNS;
    }

    @Override
    public RowKind getRowKind() {
        return toRowKind(logRecord.getChangeType());
    }

    @Override
    public void setRowKind(RowKind rowKind) {
        // do nothing
    }

    @Override
    public boolean isNullAt(int pos) {
        if (pos < originRowFieldCount) {
            return internalRow.isNullAt(pos);
        }
        // is the last three system fields: bucket, offset, timestamp which are never null
        return false;
    }

    @Override
    public boolean getBoolean(int pos) {
        return internalRow.getBoolean(pos);
    }

    @Override
    public byte getByte(int pos) {
        return internalRow.getByte(pos);
    }

    @Override
    public short getShort(int pos) {
        return internalRow.getShort(pos);
    }

    @Override
    public int getInt(int pos) {
        if (pos == originRowFieldCount) {
            // bucket system column
            return bucket;
        }
        return internalRow.getInt(pos);
    }

    @Override
    public long getLong(int pos) {
        if (pos == originRowFieldCount + 1) {
            //  offset system column
            return logRecord.logOffset();
        } else if (pos == originRowFieldCount + 2) {
            //  timestamp system column
            return logRecord.timestamp();
        }
        //  the origin RowData
        return internalRow.getLong(pos);
    }

    @Override
    public float getFloat(int pos) {
        return internalRow.getFloat(pos);
    }

    @Override
    public double getDouble(int pos) {
        return internalRow.getDouble(pos);
    }

    @Override
    public BinaryString getString(int pos) {
        return BinaryString.fromBytes(internalRow.getString(pos).toBytes());
    }

    @Override
    public Decimal getDecimal(int pos, int precision, int scale) {
        com.alibaba.fluss.row.Decimal flussDecimal = internalRow.getDecimal(pos, precision, scale);
        if (flussDecimal.isCompact()) {
            return Decimal.fromUnscaledLong(flussDecimal.toUnscaledLong(), precision, scale);
        } else {
            return Decimal.fromBigDecimal(flussDecimal.toBigDecimal(), precision, scale);
        }
    }

    @Override
    public Timestamp getTimestamp(int pos, int precision) {
        // it's timestamp system column
        if (pos == originRowFieldCount + 2) {
            return Timestamp.fromEpochMillis(logRecord.timestamp());
        }

        DataType paimonTimestampType = tableTowType.getTypeAt(pos);

        switch (paimonTimestampType.getTypeRoot()) {
            case TIMESTAMP_WITHOUT_TIME_ZONE:
                if (TimestampNtz.isCompact(precision)) {
                    return Timestamp.fromEpochMillis(
                            internalRow.getTimestampNtz(pos, precision).getMillisecond());
                } else {
                    TimestampNtz timestampNtz = internalRow.getTimestampNtz(pos, precision);
                    return Timestamp.fromEpochMillis(
                            timestampNtz.getMillisecond(), timestampNtz.getNanoOfMillisecond());
                }

            case TIMESTAMP_WITH_LOCAL_TIME_ZONE:
                if (TimestampLtz.isCompact(precision)) {
                    return Timestamp.fromEpochMillis(
                            internalRow.getTimestampLtz(pos, precision).getEpochMillisecond());
                } else {
                    TimestampLtz timestampLtz = internalRow.getTimestampLtz(pos, precision);
                    return Timestamp.fromEpochMillis(
                            timestampLtz.getEpochMillisecond(),
                            timestampLtz.getNanoOfMillisecond());
                }
            default:
                throw new UnsupportedOperationException(
                        "Unsupported data type to get timestamp: " + paimonTimestampType);
        }
    }

    @Override
    public byte[] getBinary(int pos) {
        return internalRow.getBytes(pos);
    }

    @Override
    public Variant getVariant(int pos) {
        throw new UnsupportedOperationException(
                "getVariant is not support for Fluss record currently.");
    }

    @Override
    public InternalArray getArray(int pos) {
        throw new UnsupportedOperationException(
                "getArray is not support for Fluss record currently.");
    }

    @Override
    public InternalMap getMap(int pos) {
        throw new UnsupportedOperationException(
                "getMap is not support for Fluss record currently.");
    }

    @Override
    public InternalRow getRow(int pos, int pos1) {
        throw new UnsupportedOperationException(
                "getRow is not support for Fluss record currently.");
    }
}
