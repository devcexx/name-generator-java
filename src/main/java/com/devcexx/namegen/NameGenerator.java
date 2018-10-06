package com.devcexx.namegen;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;

public class NameGenerator {
    private final TrainData trainData;
    private final WeightedRandomPicker<Group> initialPicker;
    private final Map<Group, WeightedRandomPicker<Group>> adjPickers = new HashMap<>();

    public NameGenerator(TrainData trainData) {
        this(trainData, new Random());
    }

    public NameGenerator(TrainData trainData, Random random) {
        this.trainData = trainData;
        this.initialPicker =  new WeightedRandomPicker<>(random, trainData.initialProbs);

        for (Map.Entry<Group, Map<Group, Double>> entry : trainData.adjProbs.entrySet()) {
            adjPickers.put(entry.getKey(), new WeightedRandomPicker<>(random, entry.getValue()));
        }
    }

    public String next() {
        List<Group> groups = new LinkedList<>();
        int nameLength = 0;
        String generated = null;

        while (generated == null) {
            Group last = initialPicker.next();
            groups.add(last);
            nameLength += last.group.length();

            while (!last.end) {
                last = adjPickers.get(last).next();
                nameLength += last.group.length();
                groups.add(last);
            }

            if (nameLength <= trainData.maxWordSize) {
                StringBuilder b = new StringBuilder(nameLength);
                for (Group g : groups) {
                    b.append(g.group);
                }
                generated = b.toString();
            } else {
                groups.clear();
                nameLength = 0;
            }
        }

        return generated;
    }

    public Stream<String> stream() {
        return Stream.generate(this::next);
    }

    public static void main(String[] args) throws IOException {
        NameGenerator gen = new NameGenerator(TrainData.decode(new GZIPInputStream(new FileInputStream("/Users/roberto/IdeaProjects/name-generator-java/datasets/training.gz"))));
        while (!System.out.checkError()) {
            System.out.println(gen.next());
        }
    }
}
