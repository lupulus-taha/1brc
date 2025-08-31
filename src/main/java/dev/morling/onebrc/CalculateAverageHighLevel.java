/*
 *  Copyright 2023 The original authors
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package dev.morling.onebrc;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class CalculateAverageHighLevel {
    // private static final String MEASUREMENTS = "./measurements.txt";
    private static final String MEASUREMENTS = "./measurements_trimmed.txt";

    public static void main(String[] args) {
        long start = System.nanoTime();

        Map<String, MeasurementValues> measurements = new HashMap<>();

        try(BufferedReader br = new BufferedReader(new FileReader(MEASUREMENTS))) {
            String line;
            while((line = br.readLine()) != null) {
                String[] split = line.split(";");
                if(measurements.containsKey(split[0])) {
                    MeasurementValues values = measurements.get(split[0]);
                    values.addValue(Double.parseDouble(split[1]));
                    measurements.put(split[0], values);
                }
                else {
                    double value = Double.parseDouble(split[1]);
                    measurements.put(split[0], new MeasurementValues(value));
                }
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }

        long end = System.nanoTime();

        System.out.print("{");
        int size = measurements.size();
        AtomicInteger i = new AtomicInteger(0);
        measurements.forEach((key, value) -> {
            System.out.printf("%s=%f/%f/%f", key, value.getMin(), value.getAverage(), value.getMax());
            if (i.incrementAndGet() != size) {
                System.out.print(", ");
            }
        });
        System.out.print("}\n");
        System.out.println();
        System.out.printf("Delta: %,d%n", end - start);
    }

    private static class MeasurementValues {
        private final List<Double> values = new ArrayList<>();

        public MeasurementValues(double value) {
            values.add(value);
        }

        public void addValue(double value) {
            values.add(value);
        }

        public double getMin() {
            return values.stream().min(Double::compareTo).orElseThrow();
        }

        public double getMax() {
            return values.stream().max(Double::compareTo).orElseThrow();
        }

        public double getAverage() {
            AtomicReference<Double> average = new AtomicReference<>(0.0);
            values.forEach(value -> average.updateAndGet(v -> v + value));
            return (average.get() / values.size());
        }
    }
}
