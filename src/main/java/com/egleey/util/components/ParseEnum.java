package com.egleey.util.components;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ParseEnum {

    public static <E extends Enum<E>, S> E parse(Class<E> enumType, S source, String methodName) {
        try {
            Method method = enumType.getDeclaredMethod(methodName);
            for (E e : enumType.getEnumConstants()) {
                if ( method.invoke(e).equals(source)) {
                    return e;
                }
            }
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }

        return null;
    }
}
