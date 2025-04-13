/*
 * Copyright © 2017-2019 Cask Data, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package io.cdap.wrangler.api.parser;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.cdap.wrangler.api.annotations.PublicEvolving;

/**
 * The Bool class wraps the primitive type {@code Boolean} in a object.
 * An object of type {@code Bool} contains the value in primitive type
 * as well as the type of the token this class represents.
 *
 * <p>
 * In addition, this class provides two methods one to extract the
 * value held by this wrapper object, and the second for extracting the
 * type of the token.
 * </p>
 *
 * @see BoolList
 * @see ColumnName
 * @see ColumnNameList
 * @see DirectiveName
 * @see Numeric
 * @see NumericList
 * @see Properties
 * @see Ranges
 * @see Expression
 * @see Text
 * @see TextList
 * @see Identifier
 */
@PublicEvolving
public class ByteSize implements Token {
  /**
   * The {@code Boolean} object that represents the value held by the token.
   */
  private Long value;

  /**
   * Allocates a {@code Boolean} object representing the
   * {@code value} argument.
   *
   * @param value the value of the {@code Boolean}.
   */
  public ByteSize(String value) {
    parse(value);
  }

  private void parse(String value) {
    Double number = Double.parseDouble(value.replaceAll("[^0-9.]", ""));
    String unit = value.replaceAll("[0-9.]", "").toLowerCase().trim();
    switch (unit) {
      case "b":
        this.value = number.longValue();
        break;
      case "kb":
        this.value = (long) (number * 1024);
        break;
      case "mb":
        this.value = (long) (number * 1024 * 1024);
        break;
      case "gb":
        this.value = (long) (number * 1024 * 1024 * 1024);
        break;
      case "tb":
        this.value = (long) (number * 1024 * 1024 * 1024 * 1024);
        break;
      case "pb":
        this.value = (long) (number * 1024 * 1024 * 1024 * 1024 * 1024);
        break;
      case "eb":
        this.value = (long) (number * 1024 * 1024 * 1024 * 1024 * 1024 * 1024);
        break;
      case "zb":
        this.value = (long) (number * 1024 * 1024 * 1024 * 1024 * 1024 * 1024 * 1024);
        break;
      case "yb":
        this.value = (long) (number * 1024 * 1024 * 1024 * 1024 * 1024 * 1024 * 1024 * 1024);
        break;
      default:
        throw new IllegalArgumentException("Unsupported byte size unit: " + unit + " in " + value);
    }
  }

  public Long getBytes() {
    return value;
  }

  public Double getKilobytes() {
    return (double) value / 1024;
  }

  public Double getMegabytes() {
    return (double) value / (1024 * 1024);
  }

  public Double getGigabytes() {
    return (double) value / (1024 * 1024 * 1024);
  }

  public Double getTerabytes() {
    return (double) value / (1024 * 1024 * 1024 * 1024);
  }

  public Double getPetabytes() {
    return (double) value / (1024 * 1024 * 1024 * 1024 * 1024);
  }

  public Double getExabytes() {
    return (double) value / (1024 * 1024 * 1024 * 1024 * 1024 * 1024);

  }

  public Double getZettabytes() {
    return (double) value / (1024 * 1024 * 1024 * 1024 * 1024 * 1024 * 1024);
  }

  public Double getYottabytes() {
    return (double) value / (1024 * 1024 * 1024 * 1024 * 1024 * 1024 * 1024 * 1024);
  }

  /**
   * Returns the value of this {@code Boolean} object as a boolean
   * primitive.
   *
   * @return the primitive {@code boolean} value of this object.
   */
  @Override
  public Long value() {
    return getBytes();
  }

  /**
   * Returns the type of this {@code Bool} object as a {@code TokenType}
   * enum.
   *
   * @return the enumerated {@code TokenType} of this object.
   */
  @Override
  public TokenType type() {
    return TokenType.BYTE_SIZE;
  }

  /**
   * Returns the members of this {@code Bool} object as a {@code JsonElement}.
   *
   * @return Json representation of this {@code Bool} object as
   *         {@code JsonElement}
   */
  @Override
  public JsonElement toJson() {
    JsonObject object = new JsonObject();
    object.addProperty("type", TokenType.TIME_DURATION.name());
    object.addProperty("value", value);
    return object;
  }
}
