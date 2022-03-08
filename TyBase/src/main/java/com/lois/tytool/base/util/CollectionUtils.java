package com.lois.tytool.base.util;

import java.util.List;

/**
 * CollectionUtils
 * @Author Luo.T.Y
 * @Date 2022/2/22
 * @Time 20:50
 */
public class CollectionUtils {

    public static boolean isEmpty(List<?> list) {
        return list == null || list.isEmpty();
    }

    public static boolean isNotEmpty(List<?> list) {
        return list != null && (list.size() > 0);
    }

}
