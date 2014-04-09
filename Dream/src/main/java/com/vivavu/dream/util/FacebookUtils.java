package com.vivavu.dream.util;

import com.facebook.Session;

/**
 * Created by yuja on 14. 2. 18.
 */
public class FacebookUtils {
    public static boolean isOpen(){
        Session activeSession = Session.getActiveSession();
        if(activeSession != null && activeSession.isOpened()){
            return true;
        }
        return false;
    }
}
