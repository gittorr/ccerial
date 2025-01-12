package org.gittorr.ccerial.variable.arrays;

import org.gittorr.ccerial.CcSerializable;

@CcSerializable
public class StatisticsData {

    private String[] names;

    private int[] scores;

    public StatisticsData() {
    }

    public StatisticsData(String[] names, int[] scores) {
        this.names = names;
        this.scores = scores;
    }

    public String[] getNames() {
        return names;
    }

    public void setNames(String[] names) {
        this.names = names;
    }

    public int[] getScores() {
        return scores;
    }

    public void setScores(int[] scores) {
        this.scores = scores;
    }
}
