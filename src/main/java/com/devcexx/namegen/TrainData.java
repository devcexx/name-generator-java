package com.devcexx.namegen;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class TrainData {

    public final int maxWordSize;
    public final Map<Group, Double> initialProbs;
    public final Map<Group, Map<Group, Double>> adjProbs;

    public TrainData(int maxWordSize, Map<Group, Double> initialProbs, Map<Group, Map<Group, Double>> adjProbs) {
        this.maxWordSize = maxWordSize;
        this.initialProbs = initialProbs;
        this.adjProbs = adjProbs;
    }

    public static TrainData decode(InputStream input) throws IOException {
        int maxWordSize;
        Map<Group, Double> initialProbs = new HashMap<>();
        Map<Group, Map<Group, Double>> adjProbs = new HashMap<>();
        try (DataInputStream in = new DataInputStream(new BufferedInputStream(input))) {
            maxWordSize = in.readInt();
            int nInitialGroups = in.readInt();


            for (int i = 0; i < nInitialGroups; i++) {
                String groupName = in.readUTF();
                double prob = in.readDouble();

                Group group = Group.fromGroupName(groupName);
                initialProbs.put(group, prob);
            }

            int nGroups = in.readInt();

            for (int i = 0; i < nGroups; i++) {
                String groupName = in.readUTF();
                Group group = Group.fromGroupName(groupName);

                int nadj = in.readInt();
                for (int j = 0; j < nadj; j++) {
                    String adjName = in.readUTF();
                    double adjProb = in.readDouble();

                    Group adj = Group.fromGroupName(adjName);
                    adjProbs.computeIfAbsent(group, g -> new HashMap<>()).put(adj, adjProb);
                }
            }
        }
        return new TrainData(maxWordSize, initialProbs, adjProbs);
    }

    public void write(OutputStream output) throws IOException {
        try (DataOutputStream out = new DataOutputStream(new BufferedOutputStream(output))) {
            out.writeInt(this.maxWordSize);
            out.writeInt(this.initialProbs.size());

            for (Map.Entry<Group, Double> entry : this.initialProbs.entrySet()) {
                out.writeUTF(entry.getKey().groupName());
                out.writeDouble(entry.getValue());
            }
            out.writeInt(this.adjProbs.size());
            for (Group group : this.adjProbs.keySet()) {
                Map<Group, Double> adjProbs = this.adjProbs.get(group);
                out.writeUTF(group.groupName());
                out.writeInt(adjProbs.size());
                for (Map.Entry<Group, Double> adjEntry : adjProbs.entrySet()) {
                    out.writeUTF(adjEntry.getKey().groupName());
                    out.writeDouble(adjEntry.getValue());
                }
            }
        }
    }
}
