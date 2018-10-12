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
import de.qaware.chronix.solr.type.metric.functions.transformation.utils.SamplingUtils;
import de.qaware.chronix.timeseries.MetricTimeSeries;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Optional;

/**
 * The sample average interval transformation.
 *
 * @author amarziali
 */
public final class SampleAverageInterval implements ChronixTransformation<MetricTimeSeries> {

    private final long interval;

    private final Long maxPoints;


    /**
     * divide the points sequence into equally sized buckets
     * select the average point of each bucket
     *
     * @param args the interval of the bucket (mandatory) and the optional max point to retain.
     */
    public SampleAverageInterval(String[] args) {
        this.interval = Long.parseLong(args[0]);
        this.maxPoints = args.length > 1 ? Long.parseLong(args[1]) : null;
    }

    /**
     * Transforms a time series using a moving average that is based on a window with a fixed amount of bucketSize.
     * The last window contains equals or a lower amount bucketSize.
     *
     * @param timeSeries the time series that is transformed
     */
    @Override
    public void execute(MetricTimeSeries timeSeries, FunctionValueMap functionValueMap) {
        if (timeSeries.size() > 1) {
            SamplingUtils.sample(timeSeries, interval, Optional.ofNullable(maxPoints));
        }
        functionValueMap.add(this);
    }


    @Override
    public String getQueryName() {
        return "savgint";
    }

    @Override
    public String getTimeSeriesType() {
        return "metric";
    }

    @Override
    public String[] getArguments() {
        return new String[]{
                "interval=" + interval,
                "maxPoints=" + maxPoints
        };
    }


    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("interval", interval)
                .append("maxPoints", maxPoints)
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
        SampleAverageInterval rhs = (SampleAverageInterval) obj;
        return new EqualsBuilder()
                .append(this.interval, rhs.interval)
                .append(this.maxPoints, rhs.maxPoints)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(interval)
                .append(maxPoints)
                .toHashCode();
    }
}
