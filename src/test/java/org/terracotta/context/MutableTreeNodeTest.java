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

import org.hamcrest.collection.IsEmptyCollection;
import org.hamcrest.collection.IsIterableContainingInOrder;
import org.junit.Test;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.assertThat;

/**
 * @author cdennis
 */
public class MutableTreeNodeTest {

  @Test
  public void testCleanDisconnects() {
    MutableTreeNode test = new MutableTreeNode(null);

    MutableTreeNode child = new MutableTreeNode(null);
    MutableTreeNode parent = new MutableTreeNode(null);

    test.addChild(child);
    parent.addChild(test);
    parent.addChild(child);

    test.clean();

    assertThat(test.getChildren(), IsEmptyCollection.empty());
    assertThat(test.getAncestors(), IsEmptyCollection.empty());

    assertThat(parent.getChildren(), hasSize(1));
    assertThat(parent.getChildren(), IsIterableContainingInOrder.contains(child));

    assertThat(child.getAncestors(), hasSize(1));
    assertThat(child.getAncestors(), IsIterableContainingInOrder.contains(parent));
  }
}
