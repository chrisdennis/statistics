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
package org.terracotta.statistics.derived.histogram;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.DescribedAs;
import org.junit.AssumptionViolatedException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Random;
import java.util.function.Function;
import java.util.stream.DoubleStream;

import static java.lang.Math.ceil;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.util.Arrays.asList;
import static java.util.Arrays.copyOfRange;
import static java.util.Arrays.sort;
import static java.util.Arrays.stream;
import static java.util.stream.DoubleStream.concat;
import static java.util.stream.DoubleStream.generate;
import static java.util.stream.IntStream.range;
import static org.hamcrest.collection.IsArrayContainingInOrder.arrayContaining;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.number.OrderingComparison.greaterThan;
import static org.hamcrest.number.OrderingComparison.greaterThanOrEqualTo;
import static org.hamcrest.number.OrderingComparison.lessThanOrEqualTo;
import static org.junit.Assert.assertThat;

public class BarSplittingBiasedHistogramPreciseTest extends HistogramPreciseTest {

  public BarSplittingBiasedHistogramPreciseTest(long seed, double biasRange, int bars, double[] quantiles) {
    super(seed, biasRange, bars, quantiles);
  }

  @Override
  protected Histogram histogram(double bias, int bars, long window) {
    return new BarSplittingBiasedHistogram(bias, bars, window);
  }

  @Override
  protected void feedHistogram(Histogram histogram, double[] values) {
    range(0, values.length).forEach(i -> histogram.event(values[i], i));
  }
}
