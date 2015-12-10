package com.innoq.urls.ui.domain;

public final class URL {

    @org.hibernate.validator.constraints.URL
    private String value;

    public URL() {
    }

    public URL(String value) {
        this.value = value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
