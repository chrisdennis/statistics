/*
 * Copyright Terracotta, Inc.
 * Copyright IBM Corp. 2024, 2025
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.terracotta.statistics.registry;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Ludovic Orban
 */
public final class ValueStatisticDescriptor {

  private final String observerName;
  private final Set<String> tags;

  private ValueStatisticDescriptor(String observerName, Set<String> tags) {
    this.observerName = observerName;
    this.tags = Collections.unmodifiableSet(tags);
  }

  public String getObserverName() {
    return observerName;
  }

  public Set<String> getTags() {
    return tags;
  }

  public static ValueStatisticDescriptor descriptor(String observerName, Set<String> tags) {
    return new ValueStatisticDescriptor(observerName, tags);
  }

  public static ValueStatisticDescriptor descriptor(String observerName, String... tags) {
    return new ValueStatisticDescriptor(observerName, new HashSet<>(Arrays.asList(tags)));
  }
}
