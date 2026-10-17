package com.github.tvbox.osc.util;


import java.lang.reflect.Array;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class StringUtils {

    public static boolean isEmpty(CharSequence str) {
        return str == null || str.length() == 0;
    }

    public static boolean isNotEmpty(CharSequence str) {
        return !isEmpty(str);
    }

    public static boolean isNull(Object obj) {
        return obj == null;
    }

    public static boolean isEmpty(Object obj) {
        if (obj == null) return true;
        else if (obj instanceof CharSequence) return ((CharSequence) obj).length() == 0;
        else if (obj instanceof Collection) return ((Collection) obj).isEmpty();
        else if (obj instanceof Map) return ((Map) obj).isEmpty();
        else if (obj.getClass().isArray()) return Array.getLength(obj) == 0;

        return false;
    }

    public static boolean isNotEmpty(Object obj) {
        return !isEmpty(obj);
    }

    public static String arrayToString(String[] list, int fromIndex, String cha) {
        return arrayToString(list, fromIndex, list == null ? 0 : list.length, cha);
    }

    public static String arrayToString(String[] list, int fromIndex, int endIndex, String cha) {
        StringBuilder builder = new StringBuilder();
        if (list == null || list.length <= fromIndex) {
            return "";
        } else if (list.length <= 1) {
            return list[0];
        } else {
            builder.append(list[fromIndex]);
        }
        for (int i = 1 + fromIndex; i < list.length && i < endIndex; i++) {
            builder.append(cha).append(list[i]);
        }
        return builder.toString();
    }

    public static String listToString(List<String> list, String cha) {
        StringBuilder builder = new StringBuilder();
        if (list == null || list.size() <= 0) {
            return "";
        } else if (list.size() <= 1) {
            return list.get(0);
        } else {
            builder.append(list.get(0));
        }
        for (int i = 1; i < list.size(); i++) {
            builder.append(cha).append(list.get(i));
        }
        return builder.toString();
    }

    public static String listToString(List<String> list, int fromIndex, String cha) {
        StringBuilder builder = new StringBuilder();
        if (list == null || list.size() <= fromIndex) {
            return "";
        } else if (list.size() <= 1) {
            return list.get(0);
        } else {
            builder.append(list.get(fromIndex));
        }
        for (int i = fromIndex + 1; i < list.size(); i++) {
            builder.append(cha).append(list.get(i));
        }
        return builder.toString();
    }

    public static String listToString(List<String> list) {
        return listToString(list, "&&");
    }

    public static String trim(String string) {
        if (string == null || string.length() == 0 || " ".equals(string)) return "";
        int start = 0, len = string.length();
        int end = len - 1;
        while ((start < end) && ((string.charAt(start) <= ' ') || (string.charAt(start) == '　'))) {
            ++start;
        }
        while ((start < end) && ((string.charAt(end) <= ' ') || (string.charAt(end) == '　'))) {
            --end;
        }
        if (end < len) ++end;
        return ((start > 0) || (end < len)) ? string.substring(start, end) : string;
    }

    public static boolean isJsonObject(String text) {
        boolean result = false;
        if (isNotEmpty(text)) {
            text = trim(text);
            if (text.startsWith("{") && text.endsWith("}")) {
                result = true;
            }
        }
        return result;
    }

    public static boolean isJsonArray(String text) {
        boolean result = false;
        if (isNotEmpty(text)) {
            text = trim(text);
            if (text.startsWith("[") && text.endsWith("]")) {
                result = true;
            }
        }
        return result;
    }
}
