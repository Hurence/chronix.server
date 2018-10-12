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
package de.qaware.chronix.solr.type.metric.functions.transformation.utils;

import de.qaware.chronix.timeseries.MetricTimeSeries;
import de.qaware.chronix.timeseries.dts.Point;

import java.util.List;
import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class SamplingUtils {

    public static void sample(MetricTimeSeries timeSeries, long interval, Optional<Long> maxPoints) {
        //first divide by bucket
        SortedMap<Long, List<Point>> pointsPerBucket = new TreeMap<>(timeSeries.points()
                .collect(Collectors.groupingBy(point -> Math.floorDiv(point.getTimestamp(), interval))));
        timeSeries.clear();
        //then compute new ones
        pointsPerBucket.entrySet().stream()
                .skip(Math.max(0L, pointsPerBucket.size() - maxPoints.orElse(Long.MAX_VALUE)))
                .map(entry -> new Point(entry.getKey().intValue(),
                        (entry.getKey() + 1) * interval,
                        entry.getValue().stream().mapToDouble(Point::getValue).average().getAsDouble()))
                .forEachOrdered(point -> timeSeries.add(point.getTimestamp(), point.getValue()));
    }

}

