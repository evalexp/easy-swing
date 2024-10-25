package io.github.evalexp.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * ReflectUtil for fast reflect operation
 */
public class ReflectUtil {
    /**
     * search the field with specific annotation at the obj
     * @param obj target object
     * @param annotationClass annotation class
     * @return all fields with specific annotation
     */
    public static List<Field> searchFieldByAnnotation(Object obj, Class<? extends Annotation> annotationClass) {
        Class<?> clazz = obj.getClass();
        List<Field> fields = new ArrayList<>();
        while (clazz != Object.class) {
            for (Field field : clazz.getDeclaredFields()) {
                if (field.isAnnotationPresent(annotationClass)) {
                    fields.add(field);
                }
            }
            clazz = clazz.getSuperclass();
        }
        return fields;
    }

    /**
     * search target field setter method
     * @param object target object
     * @param field target field
     * @return method if success, else null
     */
    public static Method searchSetterByField(Object object, Field field) {
        return ReflectUtil.searchMethodByNameAndType(object, "set" + StringUtil.capitalize(field.getName()), field.getType());
    }

    /**
     * search target field getter method
     * @param object target object
     * @param field target field
     * @return method if success, else null
     */
    public static Method searchGetterByField(Object object, Field field) {
        return ReflectUtil.searchMethodByNameAndType(object, "get" + StringUtil.capitalize(field.getName()));
    }

    /**
     * search method
     * @param object target object
     * @param name target method name
     * @param types method args type
     * @return method is success, else null
     */
    private static Method searchMethodByNameAndType(Object object, String name, Class<?>... types) {
        Class<?> clazz = object.getClass();
        Method method = null;
        while (clazz != Object.class) {
            try {
                method = clazz.getDeclaredMethod(name, types);
                break;
            } catch (NoSuchMethodException e) {
                clazz = clazz.getSuperclass();
            }
        }
        return method;
    }

    /**
     * search i18n text setter method
     * @param object target object
     * @param methodNames search method name
     * @return method if success, else null
     */
    public static Method searchI18NCompatibleMethod(Object object, List<String> methodNames) {
        for (String methodName : methodNames) {
            Method method = ReflectUtil.searchMethodByNameAndType(object, methodName, String.class);
            if (method != null) {
                return method;
            }
        }
        return null;
    }

    /**
     * get the class constructor with specific args
     * @param clazz target class
     * @param args construct args
     * @return constructor if found, else null
     */
    public static Constructor<?> getConstructor(Class<?> clazz, Object... args) {
        try {
            if (args.length == 0) return clazz.getConstructor();
            Constructor<?> targetConstructor = null;
            for (Constructor<?> constructor : clazz.getConstructors()) {
                if (constructor.getParameterCount() == args.length) {
                    int i = 0;
                    for (; i < args.length; i++) {
                        if (!constructor.getParameterTypes()[i].isAssignableFrom(args[i].getClass())) break;
                    }
                    if (i == args.length) {
                        targetConstructor = constructor;
                        break;
                    }
                }
            }
            return targetConstructor;
        } catch (NoSuchMethodException e) {
            return null;
        }
    }
}
