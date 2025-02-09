package org.gittorr.ccerial.variable.maps;

import org.gittorr.ccerial.CcSerializable;

import java.util.HashMap;
import java.util.Map;

@CcSerializable
public class StatisticsMapData {

    private Map<String, Integer> scores;

    public StatisticsMapData() {
        this.scores = new HashMap<>();
    }

    public StatisticsMapData(Map<String, Integer> scores) {
        this.scores = scores;
    }

    public Map<String, Integer> getScores() {
        return scores;
    }

    public void setScores(Map<String, Integer> scores) {
        this.scores = scores;
    }

}
