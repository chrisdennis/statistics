/*
 * All content copyright Terracotta, Inc., unless otherwise indicated.
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
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.IOException;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.DoubleStream;

import static java.lang.Math.ceil;
import static java.util.Arrays.asList;
import static java.util.Arrays.sort;
import static java.util.Arrays.stream;
import static java.util.stream.DoubleStream.concat;
import static java.util.stream.DoubleStream.generate;
import static org.hamcrest.collection.IsArrayContainingInOrder.arrayContaining;
import static org.hamcrest.number.OrderingComparison.greaterThan;
import static org.hamcrest.number.OrderingComparison.lessThanOrEqualTo;
import static org.junit.Assert.assertThat;

@RunWith(Parameterized.class)
public class BarSplittingBiasedHistogramPreciseTest {

  private static final double ERROR_THRESHOLD = 10;

  private static final double[] HIGH_QUANTILES = new double[] {0.5, 0.75, 0.9, 0.99};
  private static final double[] LOW_QUANTILES = stream(HIGH_QUANTILES).map(d -> 1 - d).toArray();
  private static final double[] ALL_QUANTILES = concat(stream(LOW_QUANTILES), stream(HIGH_QUANTILES)).distinct().toArray();
  private final long seed;
  private final float bias;
  private final int bars;
  private final double[] quantiles;

  @Parameterized.Parameters(name = "{index}: seed={0} bias={1}, bars={2}")
  public static Iterable<Object[]> data() {
    Random rndm = new Random();
    // seed, bias, bars
    return asList(new Object[][] {
        {rndm.nextLong(), 0.01f, 20, HIGH_QUANTILES},
        {rndm.nextLong(), 0.01f, 100, HIGH_QUANTILES},
        {rndm.nextLong(), 0.01f, 1000, HIGH_QUANTILES},

        {rndm.nextLong(), 0.1f, 20, HIGH_QUANTILES},
        {rndm.nextLong(), 0.1f, 100, HIGH_QUANTILES},
        {rndm.nextLong(), 0.1f, 1000, HIGH_QUANTILES},

        {rndm.nextLong(), 1f, 20, ALL_QUANTILES},
        {rndm.nextLong(), 1f, 100, ALL_QUANTILES},
        {rndm.nextLong(), 1f, 1000, ALL_QUANTILES},

        {rndm.nextLong(), 10f, 20, LOW_QUANTILES},
        {rndm.nextLong(), 10f, 100, LOW_QUANTILES},
        {rndm.nextLong(), 10f, 1000, LOW_QUANTILES},

        {rndm.nextLong(), 100f, 20, LOW_QUANTILES},
        {rndm.nextLong(), 100f, 100, LOW_QUANTILES},
        {rndm.nextLong(), 100f, 1000, LOW_QUANTILES},
    });
  }

  public BarSplittingBiasedHistogramPreciseTest(long seed, float biasRange, int bars, double[] quantiles) {
    this.bias = (float) Math.pow(biasRange, 1.0 / bars);
    this.bars = bars;
    this.seed = seed;
    this.quantiles = quantiles;
  }

  @Test
  public void testHistogramOfFlatDistribution() {
    Random rndm = new Random(seed);

    BarSplittingBiasedHistogram bsbh = new BarSplittingBiasedHistogram(bias, bars, Long.MAX_VALUE);

    double slope = (rndm.nextDouble() * 999.99) + 0.01;
    double offset = (rndm.nextDouble() - 0.5) * 1000;
    checkPercentiles(generate(rndm::nextDouble).map(x -> (x * slope) + offset).limit(100000), bsbh, quantiles);
  }

  @Test
  public void testHistogramOfGaussianDistribution() {
    Random rndm = new Random(seed);

    BarSplittingBiasedHistogram bsbh = new BarSplittingBiasedHistogram(bias, bars, Long.MAX_VALUE);

    double width = (rndm.nextDouble() * 999.99) + 0.01;
    double centroid = (rndm.nextDouble() - 0.5) * 1000;

    checkPercentiles(generate(rndm::nextGaussian).map(x -> (x * width) + centroid).limit(100000), bsbh, quantiles);
  }

  private void checkPercentiles(DoubleStream data, BarSplittingBiasedHistogram histogram, double ... quantiles) {
    double[] values = data.toArray();

    for (int i = 0; i < values.length; ) {
      for (int j = 0; j < 1000 && i < values.length; i++) {
        histogram.event(values[i], 0);
      }

      sort(values, 0, i);

      for (double q : quantiles) {
        double[] bounds = histogram.getQuantileBounds(q);

        double ip = (i * q) - 1;
        double ceil = ceil(ip);
        if (ip == ceil) {
          double lower = values[(int) ip];
          double upper = values[(int) (ip + 1)];
          assertThat("Quantile " + q + " after " + i, bounds, encompasses(lower, upper));
        } else {
          double value = values[(int) ceil];
          assertThat("Quantile " + q + " after " + i, bounds, encompasses(value, value));
        }
      }
    }
  }

  @SuppressWarnings("unchecked")
  private static Matcher<double[]> encompasses(double lower, double upper) {
    return new DescribedAs<double[]>("a range fully encompassing [%0, %1]",
        convertedFrom(double[].class, a -> stream(a).boxed().toArray(Double[]::new),
            arrayContaining(lessThanOrEqualTo(lower), greaterThan(upper))),
        new Object[] {lower, upper}) {
      @Override
      public void describeMismatch(Object item, Description description) {
        if (item instanceof double[] && ((double[]) item).length == 2) {
          double[] array = (double[]) item;
          description.appendText("in range [").appendValue(array[0]).appendText(", ").appendValue(array[1]).appendText(") ");
        }
        super.describeMismatch(item, description);
      }
    };
  }

  @Test
  public void testFlipFlop() throws IOException {
    BarSplittingBiasedHistogram bsbh = new BarSplittingBiasedHistogram(bias, bars, 100000);
    Random rndm = new Random(seed);

    long time = 0;

    for (int c = 0; c < 10; c++) {
      long centroid = rndm.nextInt(3000) - 1500;
      long width = rndm.nextInt(3000) + 100;

      for (double datum : rndm.doubles(100000).map(d -> (d * width) + centroid).toArray()) {
        bsbh.event(datum, time++);
      }
    }
  }

  private static <T, U> Matcher<T> convertedFrom(Class<T> type, Function<T, U> mapper, Matcher<U> matcher) {
    return new TypeSafeMatcher<T>(type) {
      @Override
      public void describeTo(Description description) {
        matcher.describeTo(description);
      }

      @Override
      protected void describeMismatchSafely(T item, Description mismatchDescription) {
        matcher.describeMismatch(mapper.apply(item), mismatchDescription);
      }

      @Override
      protected boolean matchesSafely(T item) {
        return matcher.matches(mapper.apply(item));
      }
    };
  }
}