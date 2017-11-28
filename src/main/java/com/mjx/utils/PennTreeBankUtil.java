package com.mjx.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

public class PennTreeBankUtil {

    /**
     * 构造树库文本名
     */
    public static String ensureLen(int number) {
        if (Integer.toString(number).length() == 1) {
            return "000" + number;
        } else if (Integer.toString(number).length() == 2) {
            return "00" + number;
        } else {
            return "0" + number;
        }
    }

    /**
     * 加载属性文件PennTreeBankPath.propertiesz指定的树库combined路径，PennTreeBankPath.properties位于编译路径 BasicParsing/target/classes 下
     */
    public static String getCombinedPath() throws IOException {
        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("PennTreeBankPath.properties");
        Properties properties = new Properties();
        InputStreamReader isr = new InputStreamReader(inputStream, "utf-8");
        properties.load(isr);
        return (String) properties.get("combined");
    }
}
