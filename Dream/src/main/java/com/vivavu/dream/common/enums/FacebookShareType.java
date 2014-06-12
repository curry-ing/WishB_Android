package com.vivavu.dream.common.enums;

/**
 * Created by yuja on 2014-06-06.
 */
public enum FacebookShareType {
    NONE(null, "공유안함")
    , SELF("SELF","비공개로 공유")
    , CUSTOM("CUSTOM","사용자지정 공유")
    , FRIENDS_OF_FRIENDS("FRIENDS_OF_FRIENDS","FRIENDS_OF_FRIENDS")
    , ALL_FRIENDS("ALL_FRIENDS", "전체친구에게 공개")
    , EVERYONE("EVERYONE", "전체공개");

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
            } else if(code.equals(type.code)){
                return type;
            }
        }
        return NONE;
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
