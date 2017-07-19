package me.imzack.app.end.model.bean;

public class TypeMarkPattern {

    private String patternFn;
    private String patternName;

    public TypeMarkPattern(String patternFn, String patternName) {
        setPattern(patternFn, patternName);
    }

    public TypeMarkPattern() {
        this.patternFn = null;
        this.patternName = null;
    }

    public String getPatternFn() {
        return patternFn;
    }

    public String getPatternName() {
        return patternName;
    }

    public void setPattern(String patternFn, String patternName) {
        this.patternFn = patternFn;
        this.patternName = patternName;
    }
}
