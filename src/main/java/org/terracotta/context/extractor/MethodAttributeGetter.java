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
package org.terracotta.context.extractor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

abstract class MethodAttributeGetter<T> implements AttributeGetter<T> {

  private final Method method;

  MethodAttributeGetter(Method method) {
    method.setAccessible(true);
    this.method = method;
  }

  abstract Object target();

  @Override
  @SuppressWarnings("unchecked")
  public T get() {
    try {
      return (T) method.invoke(target());
    } catch (IllegalAccessException | InvocationTargetException | IllegalArgumentException ex) {
      throw new RuntimeException(ex);
    }
  }
}
