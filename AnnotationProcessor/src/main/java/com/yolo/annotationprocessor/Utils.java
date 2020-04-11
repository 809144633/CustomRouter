package com.yolo.annotationprocessor;

import java.util.Collection;
import java.util.Map;

import javax.annotation.Nullable;

/**
 * @Author: YOLO
 * @Date: 2020/4/11 13:45
 * @Description:
 */
public class Utils {
    public static String captureName(String value) {
        char[] ch = value.toCharArray();
        if (ch[0] >= 'a' && ch[0] <= 'z') {
            ch[0] = (char) (ch[0] - 32);
        }
        return new String(ch);
    }

    public static boolean isEmpty(Collection<?> coll) {
        return coll == null || coll.isEmpty();
    }

    public static boolean isEmpty(final Map<?, ?> map) {
        return map == null || map.isEmpty();
    }

    public static boolean isEmpty(@Nullable CharSequence str) {
        return str == null || str.length() == 0;
    }


}
