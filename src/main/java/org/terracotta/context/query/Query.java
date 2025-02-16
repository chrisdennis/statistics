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
package org.terracotta.context.query;

import org.terracotta.context.TreeNode;

import java.util.Set;

/**
 * A {@code Query} instance transforms an input node set into an output node set.
 * <p>
 * Useful implementations will normally perform a sequence of graph traversal
 * and node filtering operations to generate the query result.
 */
public interface Query {

  /**
   * Transforms the {@code input} node set in to an output node set.
   *
   * @param input query input node set
   * @return the output node set
   */
  Set<TreeNode> execute(Set<TreeNode> input);
}
