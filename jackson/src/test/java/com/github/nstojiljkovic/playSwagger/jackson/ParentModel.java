package com.github.nstojiljkovic.playSwagger.jackson;

import java.util.List;

public class ParentModel {
    private String sampleField;
    private Double anotherSampleField;
    private ChildModel firstChild;
    private List<ChildModel> children;

    public ParentModel(final String sampleField, final Double anotherSampleField, final ChildModel firstChild, final List<ChildModel> children) {
        this.sampleField = sampleField;
        this.anotherSampleField = anotherSampleField;
        this.firstChild = firstChild;
        this.children = children;
    }

    public String getSampleField() {
        return sampleField;
    }

    public Double getAnotherSampleField() {
        return anotherSampleField;
    }

    public ChildModel getFirstChild() {
        return firstChild;
    }

    public List<ChildModel> getChildren() {
        return children;
    }
}
