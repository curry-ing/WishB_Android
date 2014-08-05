package com.vivavu.dream.util;

/**
 * Created by yuja on 2014-08-05.
 */
public class StringUtils {
	public static String split(String str, String pattern, int index, String defaultStr){
		String[] split = str.split("[\r\n|:]");
		String returnValue = defaultStr;
		if(split != null && split.length > 0){
			if(split.length > index) {
				returnValue = split[index];
			} else {
				returnValue = split[0];
			}
		}
		return returnValue;
	}
}
