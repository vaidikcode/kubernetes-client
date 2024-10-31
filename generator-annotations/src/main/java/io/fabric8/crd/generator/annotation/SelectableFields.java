/*
 * Copyright (C) 2015 Red Hat, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.fabric8.crd.generator.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines a selectable field for a Kubernetes Custom Resource.
 * This annotation is used to specify fields that can be used for
 * selection-based queries (e.g., filtering or listing custom resources).
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface SelectableFields {

  /**
   * The name of the selectable field.
   * If left empty, the last element in the JSON Path will be used.
   *
   * @return the field name, or an empty string if the JSON Path element should be used.
   */
  String name() default "";

  /**
   * The JSON Path pointing to the field within the custom resource.
   * This is a required field.
   *
   * @return the JSON Path of the selectable field.
   */
  String jsonPath();

  /**
   * Specifies the data type of the selectable field.
   */
  enum Type {
    STRING("string"),
    INTEGER("integer"),
    BOOLEAN("boolean"),
    DATE("date");

    private final String value;

    Type(String value) {
      this.value = value;
    }

    public String getValue() {
      return value;
    }
  }

  /**
   * Specifies the format of the selectable field.
   */
  enum Format {
    NONE(""),
    INT32("int32"),
    INT64("int64"),
    DATE("date"),
    DATE_TIME("date-time");

    private final String value;

    Format(String value) {
      this.value = value;
    }

    public String getValue() {
      return value;
    }
  }

  /**
   * The data type of the selectable field, defaulting to STRING.
   *
   * @return the data type.
   */
  Type type() default Type.STRING;

  /**
   * The format of the selectable field, defaulting to NONE.
   *
   * @return the format of the field.
   */
  Format format() default Format.NONE;

  /**
   * A description of the selectable field, useful for documentation purposes.
   *
   * @return the description of the field.
   */
  String description() default "";
}
