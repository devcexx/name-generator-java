package com.devcexx.namegen;

import java.io.*;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.zip.GZIPOutputStream;

public class TrainSetGenerator {
    private static class LinesIterator implements Iterator<String> {
        private final BufferedReader in;

        private LinesIterator(Reader in) {
            this.in = new BufferedReader(in);
        }

        private String tmpLine = null;

        @Override
        public boolean hasNext() {
            try {
                return tmpLine != null || (tmpLine = in.readLine()) != null;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public String next() {
            String line;

            if (tmpLine == null) {
                try {
                    line = in.readLine();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                line = tmpLine;
                tmpLine = null;
            }

            if (line == null) {
                throw new NoSuchElementException();
            }

            return line;
        }
    }

    public static TrainData generateTrainData(Iterable<String> input, int groupSize) {
        GroupAccumulator acc = new GroupAccumulator();

        int maxWordSize = 0;
        for (String entry : input) {
            maxWordSize = Math.max(entry.length(), maxWordSize);
            extractGroups(entry, groupSize, acc);
        }

        return new TrainData(maxWordSize,
                acc.getInitialProbs(),
                acc.getGroups().stream().collect(Collectors.toMap(Function.identity(), acc::getAdjacentsProb)));
    }

    private static void extractGroups(String input, int groupLength, GroupAccumulator acc) {
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
        GZIPOutputStream stm = new GZIPOutputStream(System.out);
        generateTrainData(() -> new LinesIterator(new InputStreamReader(System.in)), groupSize).write(stm);
        stm.flush();
    }
}
