package com.maplestory.onecard.service.util;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.math.BigDecimal;

public class BeanUtils {
    public static <T> T switchNullToEmpty(T t) {
        Class<?> cls = t.getClass();
        Field[] fields = cls.getDeclaredFields();
        AccessibleObject.setAccessible(fields, true);
        for (Field field : fields) {
            try {
                Object value = field.get(t);
                if (null == value) {
                    if (field.getType() == String.class) {
                        field.set(t, "");
                    } else if (field.getType() == Integer.class) {
                        field.set(t, 0);
                    } else if (field.getType() == BigDecimal.class) {
                        field.set(t, BigDecimal.ZERO);
                    } else if (field.getType() == Long.class) {
                        field.set(t, 0L);
                    }
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException("switchNullToEmpty异常");
            }
        }
        return t;
    }
}
