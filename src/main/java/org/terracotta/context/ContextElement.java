/*
 * Copyright Terracotta, Inc.
 * Copyright IBM Corp. 2024, 2025
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.terracotta.context;

import java.util.Map;

/**
 * A shadow context associated with a Java object.
 */
public interface ContextElement {

  /**
   * The type of the associated Java object.
   *
   * @return the associated object's class
   */
  Class<?> identifier();

  /**
   * The set of attributes for the associated Java object.
   *
   * @return the associated object's attributes
   */
  Map<String, Object> attributes();
}
