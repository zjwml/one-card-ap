package com.maplestory.onecard.service.util;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ListUtils {
    public static String StringListToString(List<String> list) {
        if (list.isEmpty()) {
            return "";
        }
        StringBuilder result = new StringBuilder(list.get(0));
        for (int i = 1; i < list.size(); i++) {
            result.append(",").append(list.get(i));
        }
        return result.toString();
    }

    public static List<String> StringToStringList(String str) {
        List<String> result = new ArrayList<>();
        if (StringUtils.isNotBlank(str)) {
            result = new ArrayList<>(Arrays.asList(str.split(",")));
        }
        return result;
    }
}
