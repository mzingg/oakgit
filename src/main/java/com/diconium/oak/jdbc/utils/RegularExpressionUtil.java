package com.diconium.oak.jdbc.utils;

import org.apache.commons.lang3.StringUtils;

public class RegularExpressionUtil {

    public static String getTableName(String command) {

        String tableName = StringUtils.EMPTY;
        String[] split = command.trim().split(StringUtils.SPACE);

        return (split.length > 2) ? split[2] : tableName;
    }
}
