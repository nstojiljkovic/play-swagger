package com.github.nstojiljkovic.playSwagger.jackson;

public class ChildModel {
    private Boolean childSampleField;
    private Integer anotherChildSampleField;

    public ChildModel(final Boolean childSampleField, final Integer anotherChildSampleField) {
        this.childSampleField = childSampleField;
        this.anotherChildSampleField = anotherChildSampleField;
    }

    public Boolean getChildSampleField() {
        return childSampleField;
    }

    public Integer getAnotherChildSampleField() {
        return anotherChildSampleField;
    }
}
