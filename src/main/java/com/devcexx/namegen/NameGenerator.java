package com.devcexx.namegen;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class NameGenerator {
    public static void main(String[] args) throws IOException {
        DecodedTrainData data = DecodedTrainData.decode(System.in);
        WeightedRandomPicker<Group> initialPicker = new WeightedRandomPicker<>(data.initialProbs);
        Map<Group, WeightedRandomPicker<Group>> adjPickers = new HashMap<>();

        for (Map.Entry<Group, Map<Group, Double>> entry : data.adjProbs.entrySet()) {
            adjPickers.put(entry.getKey(), new WeightedRandomPicker<>(entry.getValue()));
        }

        while (!System.out.checkError()) {
            List<Group> groups = new LinkedList<>();
            int nameLength = 0;

            Group last = initialPicker.next();
            groups.add(last);
            nameLength += last.group.length();

            while (!last.end) {
                last = adjPickers.get(last).next();
                nameLength += last.group.length();
                groups.add(last);
            }

            if (nameLength > data.maxWordSize) continue;

            StringBuilder b = new StringBuilder(nameLength);
            for (Group g : groups) {
                b.append(g.group);
            }

            System.out.println(b.toString());
        }


    }
}
