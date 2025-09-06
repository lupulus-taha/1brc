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

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import ch.randelshofer.fastdoubleparser.JavaDoubleParser;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

public class CalculateAverageHighLevel {
    private static final Path INPUT_PATH = Paths.get("./measurements.txt");
    //    private static final Path INPUT_PATH = Paths.get("./measurements_trimmed.txt");
    private static final String DELIMITER = ";";

    public static void main(String[] args) {
        long start = System.nanoTime();

        Object2ObjectMap<String, MeasurementStats> measurements = new Object2ObjectOpenHashMap<>();

        try(BufferedReader br = Files.newBufferedReader(INPUT_PATH, StandardCharsets.UTF_8)) {
            String line;
            while((line = br.readLine()) != null) {
                int indexOfDelimiter = line.indexOf(DELIMITER);
                String city = line.substring(0, indexOfDelimiter);
                String measurement = line.substring(indexOfDelimiter + 1);
                double value = JavaDoubleParser.parseDouble(measurement);
                MeasurementStats stats = measurements.get(city);
                if(stats == null) {
                    stats = new MeasurementStats();
                    measurements.put(city, stats);
                }
                stats.add(value);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }

        long end = System.nanoTime();

        StringJoiner joiner = new StringJoiner(", ", "{", "}");
        measurements.forEach((key, value) -> joiner.add(String.format("%s=%f/%f/%f", key, value.getMin(), value.getAverage(), value.getMax())));
        System.out.println(joiner);

        System.out.println();
        System.out.printf("Delta: %,d%n", end - start);
    }

    private static class MeasurementStats {
        private long count = 0;
        private double sum = 0.0;
        private double min = Double.POSITIVE_INFINITY;
        private double max = Double.NEGATIVE_INFINITY;

        public void add(double value) {
            count++;
            sum += value;
            if(value < min) min = value;
            if(value > max) max = value;
        }

        public double getMin() {
            if(count == 0) throw new IllegalStateException("no values");
            return min;
        }

        public double getMax() {
            if(count == 0) throw new IllegalStateException("no values");
            return max;
        }

        public double getAverage() {
            if(count == 0) throw new IllegalStateException("no values");
            return sum / (double) count;
        }
    }
}
