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
import de.qaware.chronix.timeseries.dts.Point;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * The sample average bucket transformation.
 *
 * @author thomas.bailet
 * @author amarziali
 */
public final class SampleAverageBucket implements ChronixTransformation<MetricTimeSeries> {

    private final int bucketSize;


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

        if (timeSeries.size() > 1) {

            final long interval = Math.round(Math.ceil((timeSeries.getEnd() - timeSeries.getStart()) / (double) bucketSize));
            //first divide by bucket
            Map<Long, List<Point>> pointsPerBucket = timeSeries.points().collect(Collectors.groupingBy(point -> Math.floorDiv(point.getTimestamp(), interval)));
            timeSeries.clear();
            //then compute new ones
            pointsPerBucket.entrySet().stream().sorted(Map.Entry.comparingByKey())
                    .map(entry -> new Point(entry.getKey().intValue(),
                            Math.round(entry.getValue().stream().mapToLong(Point::getTimestamp).average().getAsDouble()),
                            entry.getValue().stream().mapToDouble(Point::getValue).average().getAsDouble()))
                    .forEachOrdered(point -> timeSeries.add(point.getTimestamp(), point.getValue()));

        }

        functionValueMap.add(this);
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
