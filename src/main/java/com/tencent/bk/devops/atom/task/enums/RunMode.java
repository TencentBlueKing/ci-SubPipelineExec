package com.tencent.bk.devops.atom.task.enums;

public enum RunMode {
    SYN("syn"), ASYN("asyn");

    private String value;

    RunMode(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
