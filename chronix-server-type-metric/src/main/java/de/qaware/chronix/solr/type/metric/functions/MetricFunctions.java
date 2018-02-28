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
package de.qaware.chronix.solr.type.metric.functions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The current implemented AGGREGATIONS
 *
 * @author f.lautenschlager
 */
public class MetricFunctions {

    public static List<String> ALL = new ArrayList<>();

    static {
        Collections.addAll(ALL, "avg", "min", "max", "dev", "p", "sum", "count", "first", "last", "range", "diff", "sdiff", "integral");
        Collections.addAll(ALL, "trend", "outlier", "frequency", "fastdtw");
        Collections.addAll(ALL, "vector", "divide", "scale", "bottom", "top", "movavg", "smovavg", "savgbckt", "derivative", "nnderivative", "add", "sub", "timeshift", "distinct");

    }
}
