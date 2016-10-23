package com.zack.enderplan.model.bean;

public class TypeMarkPattern {

    private String patternId, patternName;

    public TypeMarkPattern(String patternId, String patternName) {
        this.patternId = patternId;
        this.patternName = patternName;
    }

    public String getPatternId() {
        return patternId;
    }

    public void setPatternId(String patternId) {
        this.patternId = patternId;
    }

    public String getPatternName() {
        return patternName;
    }

    public void setPatternName(String patternName) {
        this.patternName = patternName;
    }
}
