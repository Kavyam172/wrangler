/*
  *  Copyright © 2017-2019 Cask Data, Inc.
  *
  *  Licensed under the Apache License, Version 2.0 (the "License"); you may not
  *  use this file except in compliance with the License. You may obtain a copy of
  *  the License at
  *
  *  http://www.apache.org/licenses/LICENSE-2.0
  *
  *  Unless required by applicable law or agreed to in writing, software
  *  distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
  *  WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
  *  License for the specific language governing permissions and limitations under
  *  the License.
  */

package io.cdap.directives.aggregates;

import io.cdap.wrangler.TestingRig;
import io.cdap.wrangler.api.Row;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class AggregateByteTimeTest {

    @Test
    public void testAggregateTotal() throws Exception {
        String[] columns = new String[] {
                "sourceSize", "sourceTime"
        };
        String[] sourceSize = new String[] {
                "1KB", "2MB", "3GB", "4TB"
        };
        String[] sourceTime = new String[] {
                "1s", "2m", "3h", "4d"
        };

        // define the expected output in bytes and milliseconds
        String[] outputs = new String[] {
                "1024.000b", "1000.000ms"
        };

        String[] directives = new String[] {
                String.format("%s :%s :%s", AggregateByteTime.NAME, columns[0], columns[1]),
        };
        List<Row> rows = new ArrayList<>();
        for (int i = 0; i < sourceSize.length; i++) {
            rows.add(new Row().add(columns[0], sourceSize[i])
                    .add(columns[1], sourceTime[i]));
        }

        List<Row> result = TestingRig.execute(directives, rows);

        Assert.assertEquals(4, result.size());
        Assert.assertEquals(outputs[0], result.get(0).getValue(AggregateByteTime.TARGET_SIZE));
        Assert.assertEquals(outputs[1], result.get(0).getValue(AggregateByteTime.TARGET_TIME));

    }

    @Test
    public void testAggregateAverage() throws Exception {
        String aggregationType = "average";
        String[] columns = new String[] {
                "sourceSize", "sourceTime"
        };
        String[] sourceSize = new String[] {
                "1KB", "2MB", "3GB", "4TB"
        };
        String[] sourceTime = new String[] {
                "1s", "2m", "3h", "4d"
        };

        // define the expected output in bytes and milliseconds
        String[] outputs = new String[] {
                "1024.000b", "1000.000ms"
        };

        String[] directives = new String[] {
                String.format("%s :%s :%s \'%s\'", AggregateByteTime.NAME, columns[0], columns[1], aggregationType),
        };
        List<Row> rows = new ArrayList<>();
        for (int i = 0; i < sourceSize.length; i++) {
            rows.add(new Row().add(columns[0], sourceSize[i])
                    .add(columns[1], sourceTime[i]));
        }

        List<Row> result = TestingRig.execute(directives, rows);

        Assert.assertEquals(4, result.size());
        Assert.assertEquals(outputs[0], result.get(0).getValue(AggregateByteTime.TARGET_SIZE));
        Assert.assertEquals(outputs[1], result.get(0).getValue(AggregateByteTime.TARGET_TIME));

    }

    @Test
    public void testAggregateTotalWithSizeUnit() throws Exception {
        String[] columns = new String[] {
                "sourceSize", "sourceTime"
        };
        String[] sourceSize = new String[] {
                "1KB", "2MB", "3GB", "4TB"
        };
        String[] sourceTime = new String[] {
                "1s", "2m", "3h", "4d"
        };

        String[] sizeUnits = new String[] {
                "MB"
        };

        // define the expected output in bytes and milliseconds
        String[] outputs = new String[] {
                "0.001mb", "1000.000ms"
        };

        String[] directives = new String[] {
                String.format("%s :%s :%s \'%s\'", AggregateByteTime.NAME, columns[0], columns[1], sizeUnits[0]),
        };
        List<Row> rows = new ArrayList<>();
        for (int i = 0; i < sourceSize.length; i++) {
            rows.add(new Row().add(columns[0], sourceSize[i])
                    .add(columns[1], sourceTime[i]));
        }

        List<Row> result = TestingRig.execute(directives, rows);

        Assert.assertEquals(4, result.size());
        Assert.assertEquals(outputs[0], result.get(0).getValue(AggregateByteTime.TARGET_SIZE));
        Assert.assertEquals(outputs[1], result.get(0).getValue(AggregateByteTime.TARGET_TIME));
    }

    @Test
    public void testAggregateTotalWithTimeUnit() throws Exception {
        String[] columns = new String[] {
                "sourceSize", "sourceTime"
        };
        String[] sourceSize = new String[] {
                "1KB", "2MB", "3GB", "4TB"
        };
        String[] sourceTime = new String[] {
                "1s", "2m", "3h", "4d"
        };

        String[] timeUnits = new String[] {
                "h"
        };

        // define the expected output in bytes and milliseconds
        String[] outputs = new String[] {
                "1024.000b", "1000.000ms"
        };

        String[] directives = new String[] {
                String.format("%s :%s :%s \'%s\'", AggregateByteTime.NAME, columns[0], columns[1], timeUnits[0]),
        };
        List<Row> rows = new ArrayList<>();
        for (int i = 0; i < sourceSize.length; i++) {
            rows.add(new Row().add(columns[0], sourceSize[i])
                    .add(columns[1], sourceTime[i]));
        }

        List<Row> result = TestingRig.execute(directives, rows);

        Assert.assertEquals(4, result.size());
        Assert.assertEquals(outputs[0], result.get(0).getValue(AggregateByteTime.TARGET_SIZE));
        Assert.assertEquals(outputs[1], result.get(0).getValue(AggregateByteTime.TARGET_TIME));
    }

    // test for average with size unit and time unit in separate functions
    @Test
    public void testAggregateAverageWithSizeUnit() throws Exception {
        String aggregationType = "average";
        String[] columns = new String[] {
                "sourceSize", "sourceTime"
        };
        String[] sourceSize = new String[] {
                "1KB", "2MB", "3GB", "4TB"
        };
        String[] sourceTime = new String[] {
                "1s", "2m", "3h", "4d"
        };

        String[] sizeUnits = new String[] {
                "MB"
        };

        // define the expected output in bytes and milliseconds
        String[] outputs = new String[] {
                "1024.000b", "1000.000ms"
        };

        String[] directives = new String[] {
                String.format("%s :%s :%s \'%s\' \'%s\'", AggregateByteTime.NAME, 
                columns[0], columns[1], aggregationType,
                        sizeUnits[0]),
        };
        List<Row> rows = new ArrayList<>();
        for (int i = 0; i < sourceSize.length; i++) {
            rows.add(new Row().add(columns[0], sourceSize[i])
                    .add(columns[1], sourceTime[i]));
        }

        List<Row> result = TestingRig.execute(directives, rows);

        Assert.assertEquals(4, result.size());
        Assert.assertEquals(outputs[0], result.get(0).getValue(AggregateByteTime.TARGET_SIZE));
        Assert.assertEquals(outputs[1], result.get(0).getValue(AggregateByteTime.TARGET_TIME));
    }

    @Test
    public void testAggregateAverageWithTimeUnit() throws Exception {
        String aggregationType = "average";
        String[] columns = new String[] {
                "sourceSize", "sourceTime"
        };
        String[] sourceSize = new String[] {
                "1KB", "2MB", "3GB", "4TB"
        };
        String[] sourceTime = new String[] {
                "1s", "2m", "3h", "4d"
        };

        String[] timeUnits = new String[] {
                "h"
        };

        // define the expected output in bytes and milliseconds
        String[] outputs = new String[] {
                "1024.000b", "0.000h"
        };

        String[] directives = new String[] {
                String.format("%s :%s :%s \'%s\' \'%s\'", AggregateByteTime.NAME, 
                columns[0], columns[1], aggregationType,
                        timeUnits[0]),
        };
        List<Row> rows = new ArrayList<>();
        for (int i = 0; i < sourceSize.length; i++) {
            rows.add(new Row().add(columns[0], sourceSize[i])
                    .add(columns[1], sourceTime[i]));
        }

        List<Row> result = TestingRig.execute(directives, rows);

        Assert.assertEquals(4, result.size());
        Assert.assertEquals(outputs[0], result.get(0).getValue(AggregateByteTime.TARGET_SIZE));
        Assert.assertEquals(outputs[1], result.get(0).getValue(AggregateByteTime.TARGET_TIME));
    }

    @Test
    public void testAggregateTotalWithSizeUnitAndTimeUnit() throws Exception {
        String[] columns = new String[] {
                "sourceSize", "sourceTime"
        };
        String[] sourceSize = new String[] {
                "1KB", "2MB", "3GB", "4TB"
        };
        String[] sourceTime = new String[] {
                "1s", "2m", "3h", "4d"
        };

        String[] sizeUnits = new String[] {
                "MB"
        };
        String[] timeUnits = new String[] {
                "h"
        };

        // define the expected output in bytes and milliseconds
        String[] outputs = new String[] {
                "0.001mb", "0.000h"
        };

        String[] directives = new String[] {
                String.format("%s :%s :%s \'%s\' \'%s\'", AggregateByteTime.NAME, columns[0], columns[1], sizeUnits[0],
                        timeUnits[0]),
        };
        List<Row> rows = new ArrayList<>();
        for (int i = 0; i < sourceSize.length; i++) {
            rows.add(new Row().add(columns[0], sourceSize[i])
                    .add(columns[1], sourceTime[i]));
        }

        List<Row> result = TestingRig.execute(directives, rows);

        Assert.assertEquals(4, result.size());
        Assert.assertEquals(outputs[0], result.get(0).getValue(AggregateByteTime.TARGET_SIZE));
        Assert.assertEquals(outputs[1], result.get(0).getValue(AggregateByteTime.TARGET_TIME));
    }

    @Test
    public void testAggregateAverageWithSizeUnitAndTimeUnit() throws Exception {
        String aggregationType = "average";
        String[] columns = new String[] {
                "sourceSize", "sourceTime"
        };
        String[] sourceSize = new String[] {
                "1KB", "2MB", "3GB", "4TB"
        };
        String[] sourceTime = new String[] {
                "1s", "2m", "3h", "4d"
        };

        String[] sizeUnits = new String[] {
                "MB"
        };
        String[] timeUnits = new String[] {
                "h"
        };

        // define the expected output in bytes and milliseconds
        String[] outputs = new String[] {
                "1024.000b", "1000.000ms"
        };

        String[] directives = new String[] {
                String.format("%s :%s :%s \'%s\' \'%s\' \'%s\'", AggregateByteTime.NAME, columns[0], columns[1],
                        aggregationType,
                        sizeUnits[0], timeUnits[0]),
        };
        List<Row> rows = new ArrayList<>();
        for (int i = 0; i < sourceSize.length; i++) {
            rows.add(new Row().add(columns[0], sourceSize[i])
                    .add(columns[1], sourceTime[i]));
        }

        List<Row> result = TestingRig.execute(directives, rows);

        Assert.assertEquals(4, result.size());
        Assert.assertEquals(outputs[0], result.get(0).getValue(AggregateByteTime.TARGET_SIZE));
        Assert.assertEquals(outputs[1], result.get(0).getValue(AggregateByteTime.TARGET_TIME));
    }

}
