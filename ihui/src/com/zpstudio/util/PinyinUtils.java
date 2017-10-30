package com.zpstudio.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
/**
 * 拼音帮助类 
 */
public class PinyinUtils {
	/**
	 * 将字符串中的中文转化为拼音,其他字符不变
	 * 花花大神->huahuadashen
	 * @param inputString
	 * @return
	 */
	public static String getPingYin(String inputString) {
		HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
		format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
		format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		format.setVCharType(HanyuPinyinVCharType.WITH_V);

		char[] input = inputString.trim().toCharArray();
		String output = "";

		try {
			for (char curchar : input) {
				if (java.lang.Character.toString(curchar).matches(
						"[\\u4E00-\\u9FA5]+")) {
					String[] temp = PinyinHelper.toHanyuPinyinStringArray(
							curchar, format);
					output += temp[0];
				} else
					output += java.lang.Character.toString(curchar);
			}
		} catch (BadHanyuPinyinOutputFormatCombination e) {
			e.printStackTrace();
		}
		return output;
	}

	/**
	 * 汉字转换为汉语拼音首字母，英文字符不变
	 * 花花大神->hhds
	 * @param chines
	 *            汉字
	 * @return 拼音
	 */
	public static String getFirstSpell(String chinese) {  
	            StringBuffer pybf = new StringBuffer();  
	            char[] arr = chinese.toCharArray();  
	            HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();  
	            defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);  
	            defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);  
	            for (char curchar : arr) {  
                    if (curchar > 128) {  
                            try {  
                                    String[] temp = PinyinHelper.toHanyuPinyinStringArray(curchar, defaultFormat);  
                                    if (temp != null) {  
                                            pybf.append(temp[0].charAt(0));  
                                    }  
                            } catch (BadHanyuPinyinOutputFormatCombination e) {  
                                    e.printStackTrace();  
                            }  
                    } else {  
                            pybf.append(curchar);  
                    }  
            }  
	            return pybf.toString().replaceAll("\\W", "").trim();  
	    } 
	public static boolean isMatched(String pinyinInput , String cond )
	{
		return isMatched(pinyinInput , cond , true , false);
	}
	public static boolean isMatched(String input , String cond , boolean bInputPinyin , boolean bCondNeedBuild)
	{
		String pinyinInput;
		if(bInputPinyin)
		{
			pinyinInput = input;
		}
		else
		{
			pinyinInput = getPingYin(input);
		}
		String regx;
		if(bCondNeedBuild)
		{
			regx = buildRegx(cond);
		}
		else
		{
			regx = cond;
		}
		return pinyinInput.matches(regx);
	}
	public static String buildRegx(String input)
	{
		String SINGLE="(.)";
		String ANY  ="(.*)";
		String regex = ANY;
		Matcher matcher = Pattern.compile(SINGLE).matcher(input);
		while(matcher.find()){
			for(int  i = 0 ;i < matcher.groupCount() ; i++)
			{
				String s = matcher.group(i);
				String unit = getPingYin(s);
				regex += unit;
				regex += ANY;
			}
			
		}
		return regex;
	}
	
	public static boolean isNumber(String number) {
		if(number==null) return false;
	    return number.matches("\\d+");    
	}
	
	public static boolean isAlpha(String alpha) {
		if(alpha==null) return false;
	    return alpha.matches("[a-zA-Z]+");    
	}
	
	public static boolean isChinese(String chineseContent) {
		if(chineseContent==null) return false;
		return chineseContent.matches("[\u4e00-\u9fa5]+");
	}
    
	public static boolean isNumberOrAlpha(String na)
	{
		if(na==null) return false;
	    return na.matches("[0-9a-zA-Z]+");    
	}
    
}
