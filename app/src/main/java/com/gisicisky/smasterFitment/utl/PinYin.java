package com.gisicisky.smasterFitment.utl;

import java.util.ArrayList;

import com.gisicisky.smasterFitment.utl.HanziToPinyin.Token;

public class PinYin {
	//汉字返回拼音，字母原样返回，都转换为小写
	public static String getPinYin(String input) {
		ArrayList<Token> tokens = HanziToPinyin.getInstance().get(input);
		StringBuilder sb = new StringBuilder();
		if (tokens != null && tokens.size() > 0) {
			for (Token token : tokens) {
				if (Token.PINYIN == token.type) {
					sb.append(token.target);
				} else {
					sb.append(token.source);
				}
			}
		}
		String strValue = sb.toString();
		strValue = sb.substring(0, 1) +sb.substring(1).toLowerCase();
		return strValue;
	}
}
