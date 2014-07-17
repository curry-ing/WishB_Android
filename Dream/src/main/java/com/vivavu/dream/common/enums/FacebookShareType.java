package com.vivavu.dream.common.enums;

/**
 * Created by yuja on 2014-06-06.
 */
public enum FacebookShareType {
    NONE("false", "공유안함")
    , SHARE("true","공유");

    private String code;
    private String description;


    FacebookShareType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    static public FacebookShareType fromCode(String code){
        for(FacebookShareType type : FacebookShareType.values()){
            if(type.code == null && code == null){
                return type;
            } else if(type.code.equals(code)){
                return type;
            }
        }
        return NONE;
    }

    static public String[] descriptions(){
        FacebookShareType[] states = values();
        String[] description = new String[states.length];
        for (int i = 0; i < states.length; i++) {
            description[i] = states[i].description;
        }
        return description;
    }

    static public String[] names(){
        FacebookShareType[] states = values();
        String[] names = new String[states.length];
        for (int i = 0; i < states.length; i++) {
            names[i] = states[i].name();
        }
        return names;
    }
}
