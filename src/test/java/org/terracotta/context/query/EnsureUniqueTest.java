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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.terracotta.context.TreeNode;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.junit.Assert.assertThat;
import static org.terracotta.context.query.QueryBuilder.queryBuilder;
import static org.terracotta.context.query.QueryTestUtils.A;
import static org.terracotta.context.query.QueryTestUtils.B;
import static org.terracotta.context.query.QueryTestUtils.createTreeNode;

@RunWith(Parameterized.class)
public class EnsureUniqueTest {

  private final Query query;

  public EnsureUniqueTest(Query query) {
    this.query = query;
  }

  @Parameterized.Parameters
  public static List<Object[]> queries() {
    return Arrays.asList(new Object[][]{{EnsureUnique.INSTANCE}, {queryBuilder().ensureUnique().build()}});
  }

  @Test
  public void testUniqueInput() {
    TreeNode node = createTreeNode(A.class);
    Set<TreeNode> results = query.execute(Collections.singleton(node));
    assertThat(results, hasSize(1));
    assertThat(results, hasItem(node));
  }

  @Test(expected = IllegalStateException.class)
  public void testNonUniqueInput() {
    Set<TreeNode> nodes = new HashSet<>();
    nodes.add(createTreeNode(A.class));
    nodes.add(createTreeNode(B.class));
    query.execute(nodes);
  }

  @Test(expected = IllegalStateException.class)
  public void testEmptyInput() {
    query.execute(Collections.emptySet());
  }
}
