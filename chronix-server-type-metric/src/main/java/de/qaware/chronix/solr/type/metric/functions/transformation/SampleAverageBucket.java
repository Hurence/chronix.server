/*
 * Copyright (C) 2016 QAware GmbH
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package de.qaware.chronix.solr.type.metric.functions.transformation;

import de.qaware.chronix.server.functions.ChronixTransformation;
import de.qaware.chronix.server.functions.FunctionValueMap;
import de.qaware.chronix.timeseries.MetricTimeSeries;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * The sample average bucket transformation.
 *
 * @author thomas.bailet
 */
public final class SampleAverageBucket implements ChronixTransformation<MetricTimeSeries> {

    private final int bucketSize;


    /**
     * divide the points sequence into equally sized buckets
     * select the average point of each bucket
     *
     * = OPENTSDB one
     *
     * @param rawData
     * @param bucketSize
     * @return

    def averageBucket(rawData: List[(Long, Double)], bucketSize: Int): List[(Long, Double)] = {

        // simple average to 100 data points
        val realBucketSize = fitBucketSize(rawData, bucketSize)
        val buckets = rawData.grouped(realBucketSize)

        val averaged = buckets.map { l =>
            val sum = l.map(_._2).sum
            val avg = sum / l.size
            l.head._1 -> avg
        }

        averaged.toList
    }
     */
    /**
     * divide the points sequence into equally sized buckets
     * select the average point of each bucket
     *
     * @param args the amount of bucketSize within a sliding window
     */
    public SampleAverageBucket(String[] args) {
        this.bucketSize = Integer.parseInt(args[0]);
    }

    /**
     * Transforms a time series using a moving average that is based on a window with a fixed amount of bucketSize.
     * The last window contains equals or a lower amount bucketSize.
     *
     * @param timeSeries the time series that is transformed
     */
    @Override
    public void execute(MetricTimeSeries timeSeries, FunctionValueMap functionValueMap) {

        // we need a sorted time series
        timeSeries.sort();

        // get the raw values as arrays
        double[] values = timeSeries.getValuesAsArray();
        long[] times = timeSeries.getTimestampsAsArray();

        int timeSeriesSize = timeSeries.size();
        // remove the old values
        timeSeries.clear();

        // the start is already set
        for (int start = 0; start < timeSeriesSize; start += bucketSize) {

            int end = start + bucketSize;
            //calculate the average of the values and the time
            evaluteAveragesAndAddToTimeSeries(timeSeries, values, times, start, end);

            //check if window end is larger than time series
            if (end + bucketSize >= timeSeriesSize) {
                evaluteAveragesAndAddToTimeSeries(timeSeries, values, times, start + bucketSize, timeSeriesSize);
                break;
            }
        }

        functionValueMap.add(this);
    }

    /**
     * Calculates the average time stamp and value for the given window (start, end) and adds it to the given time series
     *
     * @param timeSeries the time series to add the moving averages
     * @param values     the values
     * @param times      the time stamps
     * @param startIdx   the start index of the window
     * @param end        the end index of the window
     */
    private void evaluteAveragesAndAddToTimeSeries(MetricTimeSeries timeSeries, double[] values, long[] times, int startIdx, int end) {

        //If the indices are equals, just return the value at the index position
        if (startIdx == end) {
            timeSeries.add(times[startIdx], values[startIdx]);
        }

        double valueSum = 0;
        long timeSum = 0;


        for (int i = startIdx; i < end; i++) {
            valueSum += values[i];
            timeSum += times[i];
        }
        int amount = end - startIdx;

        timeSeries.add(timeSum / amount, valueSum / amount);
    }


    @Override
    public String getQueryName() {
        return "savgbckt";
    }

    @Override
    public String getTimeSeriesType() {
        return "metric";
    }

    @Override
    public String[] getArguments() {
        return new String[]{"bucketSize=" + bucketSize};
    }


    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("bucketSize", bucketSize)
                .toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }
        SampleAverageBucket rhs = (SampleAverageBucket) obj;
        return new EqualsBuilder()
                .append(this.bucketSize, rhs.bucketSize)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(bucketSize)
                .toHashCode();
    }
}
