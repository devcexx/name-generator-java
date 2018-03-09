package com.devcexx.namegen;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;

public class DecodedTrainData {

    public final int maxWordSize;
    public final Map<Group, Double> initialProbs;
    public final Map<Group, Map<Group, Double>> adjProbs;

    public DecodedTrainData(int maxWordSize, Map<Group, Double> initialProbs, Map<Group, Map<Group, Double>> adjProbs) {
        this.maxWordSize = maxWordSize;
        this.initialProbs = initialProbs;
        this.adjProbs = adjProbs;
    }

    public static DecodedTrainData decode(InputStream input) throws IOException {
        int maxWordSize;
        Map<Group, Double> initialProbs = new HashMap<>();
        Map<Group, Map<Group, Double>> adjProbs = new HashMap<>();
        try (DataInputStream in = new DataInputStream(new BufferedInputStream(new GZIPInputStream(input)))) {
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
        return new DecodedTrainData(maxWordSize, initialProbs, adjProbs);
    }
}
