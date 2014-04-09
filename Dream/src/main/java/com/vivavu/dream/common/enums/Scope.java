package com.vivavu.dream.common.enums;

/**
 * Created by yuja on 14. 1. 26.
 */
public enum Scope {
    PLAN("PLAN","PLAN"), YEALY("YEALY","YEALY"), DECADE("DECADE","DECADE");

    private final String code;
    private final String value;

    Scope(String code, String value) {
        this.code = code;
        this.value = value;
    }

    @Override
    public String toString() {
        return "Scope{" +
                "code='" + code + '\'' +
                ", value='" + value + '\'' +
                '}';
    }

    public String getCode() {
        return code;
    }

    public String getValue() {
        return value;
    }
}
