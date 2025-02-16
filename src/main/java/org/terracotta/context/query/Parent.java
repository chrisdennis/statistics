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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

class Parent implements Query {

  static final Query INSTANCE = new Parent();

  @Override
  public Set<TreeNode> execute(Set<TreeNode> input) {
    Set<TreeNode> output = new HashSet<>();
    for (TreeNode node : input) {
      List<? extends TreeNode> pathes = node.getPath();
      if (pathes.size() > 1) {
        output.add(pathes.get(pathes.size() - 2));
      }
    }
    return output;
  }

  @Override
  public String toString() {
    return "parent";
  }
}
