package com.devcexx.namegen;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class GroupAccumulator {
    private static class GroupEntry {
        private int count = 0;
        private final Map<Group, Integer> adjs = new HashMap<>();

        public void addAdjacent(Group adj) {
            count++;
            adjs.compute(adj, (g, v) -> (v == null ? 0 : v) + 1);
        }

        public double adjProb(Group adj) {
            return adjs.getOrDefault(adj, 0) / (double)count;
        }
    }

    private final Map<Group, GroupEntry> groups = new HashMap<>();
    private int initialGroupsCount = 0;

    public void add(Group group, Group adjacent) {
        if (group.start) {
            initialGroupsCount++;
        }
        groups.computeIfAbsent(group, g -> new GroupEntry()).addAdjacent(adjacent);
    }

    public void add(Group group) {
        if (group.start) {
            initialGroupsCount++;
        }
        groups.computeIfAbsent(group, g -> { GroupEntry e = new GroupEntry(); e.count++; return e; });
    }

    public double initialProb(Group group) {
        if (!group.start) return 0.0;

        return groups.computeIfAbsent(group, g -> new GroupEntry()).count / (double) initialGroupsCount;
    }

    public Map<Group, Double> getInitialProbs() {
        Map<Group, Double> map = new HashMap<>();
        for (Group group : groups.keySet()) {
            if (group.start) {
                map.put(group, initialProb(group));
            }
        }

        return map;
    }

    public Set<Group> getGroups() {
        return groups.keySet();
    }

    public Map<Group, Double> getAdjacentsProb(Group group) {
        Map<Group, Double> map = new HashMap<>();
        GroupEntry entry = groups.computeIfAbsent(group, g -> new GroupEntry());

        for (Group adj : entry.adjs.keySet()) {
            map.put(adj, entry.adjProb(adj));
        }

        return map;
    }
}
