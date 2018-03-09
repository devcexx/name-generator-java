package com.devcexx.namegen;

import java.util.*;

public class WeightedRandomPicker<A> {
    public static class Entry<A> {
        public double prob;
        public A entry;

        private Entry(double prob, A entry) {
            this.prob = prob;
            this.entry = entry;
        }

        @Override
        public String toString() {
            return "Entry{" +
                    "prob=" + prob +
                    ", entry=" + entry +
                    '}';
        }
    }

    private final Random random;
    public final List<Entry<A>> entries;

    private List<Entry<A>> buildEntries(Map<A, Double> entries) {
        List<Entry<A>> r = new ArrayList<>(entries.size());

        for (Map.Entry<A, Double> entry : entries.entrySet()) {
            r.add(new Entry<>(entry.getValue(), entry.getKey()));
        }

        r.sort((o1, o2) -> (int) Math.signum(o2.prob - o1.prob));

        double acc = 0.0;
        for (int i = r.size() - 1; i >= 0; i--) {
            if (i == 0) {
                r.get(i).prob = 1;
            } else {
                acc += r.get(i).prob;
                r.get(i).prob = acc;
            }
        }

        return r;
    }

    public WeightedRandomPicker(Random random, Map<A, Double> entries) {
        this.random = random;
        this.entries = buildEntries(entries);
    }

    public WeightedRandomPicker(Map<A, Double> entries) {
        this(new Random(), entries);
    }

    public A next() {
        double rand = random.nextDouble();
        for (int i = 0; i < entries.size(); i++) {
            if (i > 0 && entries.get(i).prob < rand) {
                return entries.get(i - 1).entry;
            }
        }
        return entries.get(entries.size() - 1).entry;
    }
}
