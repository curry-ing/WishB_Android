package com.vivavu.dream.common.enums;

/**
 * Created by yuja on 14. 1. 24.
 */
public enum RepeatType {
    NONE("NONE", "Nothing"), WKRP("WRKP","every Week"), WEEK("WEEK","in a WEEK"), MNTH("MNTH","in a Month");

    private String code;
    private String value;

    RepeatType(String code, String value) {
        this.code = code;
        this.value = value;
    }

    static public RepeatType fromCode(String code){
        for(RepeatType type : RepeatType.values()){
            if(type.code.equals(code)){
                return type;
            }
        }
        return WKRP;
    }

    public String getCode() {
        return code;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
}
