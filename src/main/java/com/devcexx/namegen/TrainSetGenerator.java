package com.devcexx.namegen;

import java.io.*;
import java.util.Map;
import java.util.zip.GZIPOutputStream;

public class TrainSetGenerator {
    public static void extractGroups(String input, int groupLength, GroupAccumulator acc) {
        for (int i = 0; i < input.length(); i++) {
            int endindex = Math.min(i + groupLength, input.length());
            String groupStr = input.substring(i, endindex);
            boolean start = i == 0;
            boolean end = endindex >= input.length();

            Group group = new Group(groupStr, start, end);

            if (end) {
                acc.add(group);
            } else {
                endindex = Math.min(i + 2 * groupLength, input.length());
                String nextStr = input.substring(i + groupLength, endindex);
                end = endindex >= input.length();

                Group nextGroup = new Group(nextStr, false, end);
                acc.add(group, nextGroup);
            }
        }
    }

    private static void printAccumulator(GroupAccumulator acc) {
        for (Map.Entry<Group, Double> entry : acc.getInitialProbs().entrySet()) {
            System.out.println(entry.getKey() + " :: " + entry.getValue());
            for (Map.Entry<Group, Double> adjEntry : acc.getAdjacentsProb(entry.getKey()).entrySet()) {
                System.out.println("    " + adjEntry.getKey() + " :: " + adjEntry.getValue());
            }
        }
    }

    public static void main(String[] args) throws IOException {

        int groupSize;
        if (args.length < 1) {
            groupSize = 1;
        } else {
            try {
                groupSize = Integer.parseInt(args[0]);
                if (groupSize < 1 || groupSize > 10) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException ex) {
                System.err.println("Invalid group size: " + args[0]);
                System.exit(1);
                return;
            }
        }

        System.err.println("Generating GZIPped " + groupSize + "-group train data from stdin dataset.");

        GroupAccumulator acc = new GroupAccumulator();
        int maxWordSize = 0;
        try (BufferedReader in = new BufferedReader(new InputStreamReader(System.in))) {
            String line;
            while ((line = in.readLine()) != null) {
                maxWordSize = Math.max(line.length(), maxWordSize);
                extractGroups(line, groupSize, acc);
            }
        }

        DataOutputStream stm = new DataOutputStream(new BufferedOutputStream(new GZIPOutputStream(System.out)));
        Map<Group, Double> probs = acc.getInitialProbs();

        stm.writeInt(maxWordSize);
        stm.writeInt(probs.size());

        for (Map.Entry<Group, Double> entry : probs.entrySet()) {
            stm.writeUTF(entry.getKey().groupName());
            stm.writeDouble(entry.getValue());
        }

        stm.writeInt(acc.getGroups().size());
        for (Group group : acc.getGroups()) {
            Map<Group, Double> adjProbs = acc.getAdjacentsProb(group);
            stm.writeUTF(group.groupName());
            stm.writeInt(adjProbs.size());
            for (Map.Entry<Group, Double> adjEntry : adjProbs.entrySet()) {
                stm.writeUTF(adjEntry.getKey().groupName());
                stm.writeDouble(adjEntry.getValue());
            }
        }
        stm.flush();
        stm.close();
    }
}
