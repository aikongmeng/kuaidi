package com.kuaibao.skuaidi.texthelp;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextToLink {
    /**
     * URL转换为链接
     * @author gudd
     * @param urlText
     * @return String
     */
    public static String urlToLink(String urlText){
    	//将空格，换行符转换为html标签格式，为什么接口不直接返回html格式？
    	urlText=urlText.replaceAll(" ", "&#160;").replaceAll("\n", "<br />");
    	// 匹配的条件选项为结束为空格(半角和全角)、换行符、字符串的结尾或者遇到其他格式的文本
        String regexp  = "((http[s]{0,1}|ftp)://[a-zA-Z0-9\\.\\-]+\\.([a-zA-Z]{2,4})(:\\d+)?(/[a-zA-Z0-9\\.\\-~!@#$%^&*+?:_/=<>]*)?)|(www.[a-zA-Z0-9\\.\\-]+\\.([a-zA-Z]{2,4})(:\\d+)?(/[a-zA-Z0-9\\.\\-~!@#$%^&*+?:_/=<>]*)?)";                                     // 结束条件
        Pattern pattern = Pattern.compile(regexp, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(urlText);
        
        String resultText = "";
        int lastEnd = 0;
        
        while(matcher.find()){
        	resultText += urlText.substring(lastEnd, matcher.start()-1);
        	resultText += "<a href=\"" + matcher.group() + "\">" + matcher.group() + "</a>";
        	lastEnd = matcher.end();
        }
        resultText += urlText.substring(lastEnd);
        return resultText;
    }
}
