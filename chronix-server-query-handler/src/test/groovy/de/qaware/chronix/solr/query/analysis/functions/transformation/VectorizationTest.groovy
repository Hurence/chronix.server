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
package de.qaware.chronix.solr.query.analysis.functions.transformation

import de.qaware.chronix.solr.query.analysis.functions.FunctionType
import de.qaware.chronix.timeseries.MetricTimeSeries
import spock.lang.Shared
import spock.lang.Specification

import java.time.Instant
import java.time.temporal.ChronoUnit

/**
 * Unit test for the vector transformation
 * @author f.lautenschlager
 */
class VectorizationTest extends Specification {

    @Shared
    def vectorization = new Vectorization()

    def "test transform"() {
        given:
        def timeSeriesBuilder = new MetricTimeSeries.Builder("Vector")

        def now = Instant.now()

        100.times {
            timeSeriesBuilder.point(now.plus(it, ChronoUnit.SECONDS).toEpochMilli(), it + 1)
        }

        when:
        def vectorizedTimeSeries = vectorization.transform(timeSeriesBuilder.build())

        then:
        vectorizedTimeSeries.size() == 2
    }

    def "test transform - 0 points"() {
        given:
        def timeSeriesBuilder = new MetricTimeSeries.Builder("Vector")

        when:
        def vectorizedTimeSeries = vectorization.transform(timeSeriesBuilder.build())

        then:
        vectorizedTimeSeries.size() == 0
    }

    def "test transform - 1..3 Points"() {
        given:
        def timeSeriesBuilder1 = new MetricTimeSeries.Builder("Vector")
        def timeSeriesBuilder2 = new MetricTimeSeries.Builder("Vector")
        def timeSeriesBuilder3 = new MetricTimeSeries.Builder("Vector")

        def now = Instant.now()

        when:
        1.times {
            timeSeriesBuilder1.point(now.plus(it, ChronoUnit.SECONDS).toEpochMilli(), it + 1)
        }

        2.times {
            timeSeriesBuilder2.point(now.plus(it, ChronoUnit.SECONDS).toEpochMilli(), it + 1)
        }

        3.times {
            timeSeriesBuilder3.point(now.plus(it, ChronoUnit.SECONDS).toEpochMilli(), it + 1)
        }

        def vectorized1 = vectorization.transform(timeSeriesBuilder1.build())
        def vectorized2 = vectorization.transform(timeSeriesBuilder2.build())
        def vectorized3 = vectorization.transform(timeSeriesBuilder3.build())

        then:
        vectorized1.size() == 1
        vectorized2.size() == 2
        vectorized3.size() == 3
    }

    def "test type"() {
        when:
        def vectorization = new Vectorization();
        then:
        vectorization.getType() == FunctionType.VECTOR
    }
}