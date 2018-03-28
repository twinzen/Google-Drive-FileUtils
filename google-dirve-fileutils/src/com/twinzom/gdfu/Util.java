package com.twinzom.gdfu;

import java.util.List;

public class Util {

	/**
	 * Format List<String> to a string line
	 * 
	 * @param list
	 * @param delimiter
	 * @param quotechar
	 * @return
	 */
	public static String listToString (List<String> list, String delimiter, String quotechar) {
		String result = "";
		for (int i=0; i<list.size(); i++) {
			result += quotechar+list.get(i)+quotechar;
			if (i+1 < list.size()) {
				result += ",";
			}
		}
		return result;
	}
	
}
