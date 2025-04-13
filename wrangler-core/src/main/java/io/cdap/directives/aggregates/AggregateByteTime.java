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

import io.cdap.cdap.api.annotation.Description;
import io.cdap.cdap.api.annotation.Name;
import io.cdap.cdap.api.annotation.Plugin;
import io.cdap.wrangler.api.Arguments;
import io.cdap.wrangler.api.Directive;
import io.cdap.wrangler.api.DirectiveExecutionException;
import io.cdap.wrangler.api.DirectiveParseException;
import io.cdap.wrangler.api.ExecutorContext;
import io.cdap.wrangler.api.Optional;
import io.cdap.wrangler.api.Row;
import io.cdap.wrangler.api.annotations.Categories;
import io.cdap.wrangler.api.parser.ByteSize;
import io.cdap.wrangler.api.parser.ColumnName;
import io.cdap.wrangler.api.parser.TimeDuration;
import io.cdap.wrangler.api.parser.TokenType;
import io.cdap.wrangler.api.parser.UsageDefinition;

import java.util.ArrayList;
import java.util.List;

/**
 * A directive for aggregating byte sizes and time durations.
 */
@Plugin(type = Directive.TYPE)
@Name(AggregateByteTime.NAME)
@Categories(categories = { "column" })
@Description("Aggregates byte sizes and time durations into total or average values.")
public class AggregateByteTime implements Directive {
  public static final String NAME = "aggregate";
  private ColumnName sourceSize;
  private ColumnName sourceTime;
  private String outputUnitSize = "b";
  private String outputUnitTime = "ms";
  private String aggregationType = "total";

  public static final String TARGET_SIZE = "resultSize";
  public static final String TARGET_TIME = "resultTime";

  @Override
  public UsageDefinition define() {
    UsageDefinition.Builder builder = UsageDefinition.builder(NAME);
    builder.define("sourceSize", TokenType.COLUMN_NAME);
    builder.define("sourceTime", TokenType.COLUMN_NAME);
    builder.define("outputUnitSize", TokenType.TEXT, Optional.TRUE);
    builder.define("outputUnitTime", TokenType.TEXT, Optional.TRUE);
    builder.define("aggregationType", TokenType.TEXT, Optional.TRUE);
    return builder.build();
  }

  @Override
  public void initialize(Arguments args) throws DirectiveParseException {
    this.sourceSize = ((ColumnName) args.value("sourceSize"));
    this.sourceTime = ((ColumnName) args.value("sourceTime"));
    if (args.contains("outputUnitSize")) {
      this.outputUnitSize = args.value("outputUnitSize").value().toString();
    }
    if (args.contains("outputUnitTime")) {
      this.outputUnitTime = args.value("outputUnitTime").value().toString();
    }
    if (args.contains("aggregationType")) {
      this.aggregationType = args.value("aggregationType").value().toString();
    }
  }

  @Override
  public void destroy() {
    // no-op
  }

  @Override
  public List<Row> execute(List<Row> rows, ExecutorContext context) throws DirectiveExecutionException {
    long totalSize = 0;
    long totalTime = 0;
    int rowCount = 0;

    for (Row row : rows) {
      Object sizeValue = row.getValue(sourceSize.value());
      Object timeValue = row.getValue(sourceTime.value());

      if (sizeValue == null || timeValue == null) {
        throw new DirectiveExecutionException(NAME, "Source columns contain null values.");
      }

      long size = convertToBytes(sizeValue);
      long time = convertToMilliSeconds(timeValue);

      totalSize += size;
      totalTime += time;
      rowCount++;
    }

    // Perform aggregation based on the type
    if (aggregationType.equalsIgnoreCase("average")) {
      totalSize = totalSize / rowCount;
      totalTime = totalTime / rowCount;
    }

    // Convert totals to the desired output units
    String resultSize = convertFromBytes(totalSize, outputUnitSize);
    String resultTime = convertFromMilliSeconds(totalTime, outputUnitTime);

    // Create a new row with the aggregated values
    Row resultRow = new Row();
    resultRow.add(TARGET_SIZE, resultSize);
    resultRow.add(TARGET_TIME, resultTime);

    // Return a single-row list containing the result
    List<Row> result = new ArrayList<>();
    result.add(resultRow);
    return result;
  }

  private long convertToBytes(Object value) throws DirectiveExecutionException {
    if (value instanceof ByteSize) {
      return ((ByteSize) value).value();
    }
    if (value instanceof String) {
      return new ByteSize((String) value).value();
    }
    if (value instanceof Number) {
      return ((Number) value).longValue();
    } else {
      throw new DirectiveExecutionException(NAME, "Invalid byte size format: " + value);
    }
  }

  private long convertToMilliSeconds(Object value) throws DirectiveExecutionException {
    if (value instanceof TimeDuration) {
      return ((TimeDuration) value).value();
    }
    if (value instanceof String) {
      return new TimeDuration((String) value).value();
    } else {
      throw new DirectiveExecutionException(NAME, "Invalid time duration format: " + value);
    }
  }

  private String convertFromBytes(long value, String unit) {
    switch (unit.toLowerCase()) {
      case "kb":
        return String.format("%.3fkb", value / 1024.0);
      case "mb":
        return String.format("%.3fmb", value / (1024.0 * 1024));
      case "gb":
        return String.format("%.3fgb", value / (1024.0 * 1024 * 1024));
      case "tb":
        return String.format("%.3ftb", value / (1024.0 * 1024 * 1024 * 1024));
      case "pb":
        return String.format("%.3fpb", value / (1024.0 * 1024 * 1024 * 1024 * 1024));
      case "eb":
        return String.format("%.3feb", value / (1024.0 * 1024 * 1024 * 1024 * 1024 * 1024));
      case "zb":
        return String.format("%.3fzb", value / (1024.0 * 1024 * 1024 * 1024 * 1024 * 1024 * 1024));
      case "yb":
        return String.format("%.3fyb", value / (1024.0 * 1024 * 1024 * 1024 * 1024 * 1024 * 1024 * 1024));
      default:
        return String.format("%.3fb", (double) value);
    }
  }

  private String convertFromMilliSeconds(long value, String unit) {
    switch (unit.toLowerCase()) {
      case "s":
        return String.format("%.3fs", value / 1000.0);
      case "m":
        return String.format("%.3fm", value / (1000.0 * 60));
      case "h":
        return String.format("%.3fh", value / (1000.0 * 60 * 60));
      case "d":
        return String.format("%.3fd", value / (1000.0 * 60 * 60 * 24));
      case "w":
        return String.format("%.3fw", value / (1000.0 * 60 * 60 * 24 * 7));
      case "mo":
        return String.format("%.3fmo", value / (1000.0 * 60 * 60 * 24 * 30));
      case "y":
        return String.format("%.3fy", value / (1000.0 * 60 * 60 * 24 * 365));
      default:
        return String.format("%.3fms", (double) value);
    }
  }
}
