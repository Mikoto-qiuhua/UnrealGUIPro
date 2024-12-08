package org.qiuhua.UnrealGUIPro.application;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CustomValueConvert {

    public static String value(String input, List<String> customValue){
        // 使用正则表达式匹配 %content%
        Pattern pattern = Pattern.compile("%(.*?)%");
        Matcher matcher = pattern.matcher(input);

        // 逐个匹配并替换
        StringBuffer result = new StringBuffer();
        while (matcher.find()) {
            // 获取匹配到的内容，去掉 %
            String matchedContent = matcher.group(1);
            customValue.add(matchedContent);
            // 替换为 {custom_内容}
            matcher.appendReplacement(result, "{custom_" + matchedContent + "}");
        }
        matcher.appendTail(result);

        // 输出替换后的字符串
        return result.toString();
    }

    public static String value(String input, Map<String, String> customValue){
        // 使用正規表示式匹配 %content%
        Pattern pattern = Pattern.compile("%(.*?)%");
        Matcher matcher = pattern.matcher(input);

        // 逐一匹配並替換
        StringBuffer result = new StringBuffer();
        while (matcher.find()) {
            // 取得符合到的內容，去掉 %
            String matchedContent = matcher.group(1);
            String replaceMatchedContent = matchedContent.replace("{", "_").replace("}", "_");
            customValue.put(matchedContent, replaceMatchedContent);
            // 替換為 {custom_內容}
            matcher.appendReplacement(result, "{custom_" + replaceMatchedContent + "}");
        }
        matcher.appendTail(result);

        // 輸出替換後的字串
        return result.toString();
    }




    //NBT用
    public static String valueNBT(String input, Map<String, String> customValue){
        // 使用正規表示式匹配 %content%
        Pattern pattern = Pattern.compile("%(.*?)%");
        Matcher matcher = pattern.matcher(input);

        // 逐一匹配並替換
        StringBuffer result = new StringBuffer();
        while (matcher.find()) {
            // 取得符合到的內容，去掉 %
            String matchedContent = matcher.group(1);
            customValue.put(matchedContent, matchedContent);
            // 替換為 {custom_內容}
            matcher.appendReplacement(result, "{custom_" + matchedContent + "}");
        }
        matcher.appendTail(result);

        // 輸出替換後的字串
        return result.toString();
    }

}
