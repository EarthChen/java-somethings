package com.earthchen.somethings;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;


public final class LambdaUtil {

    /**
     * 字段映射
     */
    private static final Map<String, Map<String, String>> COLUMN_CACHE_MAP = new ConcurrentHashMap<>();

    /**
     * SerializedLambda 反序列化缓存
     */
    private static final Map<String, WeakReference<SerializedLambda>> FUNC_CACHE = new ConcurrentHashMap<>();

    /**
     * 解析 lambda 表达式, 该方法只是调用了，在此基础上加了缓存。
     * 该缓存可能会在任意不定的时间被清除
     *
     * @param func 需要解析的 lambda 对象
     * @param <T>  类型，被调用的 Function 对象的目标类型
     * @return 返回解析后的结果
     */
    public static <T> SerializedLambda resolve(SFunction<T, ?> func) {
        Class<?> clazz = func.getClass();
        String canonicalName = clazz.getCanonicalName();
        return Optional.ofNullable(FUNC_CACHE.get(canonicalName))
                .map(WeakReference::get)
                .orElseGet(() -> {
                    SerializedLambda lambda = SerializedLambda.resolve(func);
                    FUNC_CACHE.put(canonicalName, new WeakReference<>(lambda));
                    return lambda;
                });
    }

    public static String methodToProperty(String name) {
        if (name.startsWith("is")) {
            name = name.substring(2);
        } else {
            if (!name.startsWith("get") && !name.startsWith("set")) {
                throw new RuntimeException("Error parsing property name '" + name + "'.  Didn't start with 'is', 'get' or 'set'.");
            }

            name = name.substring(3);
        }

        if (name.length() == 1 || name.length() > 1 && !Character.isUpperCase(name.charAt(1))) {
            name = name.substring(0, 1).toLowerCase(Locale.ENGLISH) + name.substring(1);
        }

        return name;
    }


    public static <T> String getFieldName(SFunction<T, ?> func) {
        return methodToProperty(resolve(func).getImplMethodName());
    }

    private static Map<String, String> createColumnCacheMap(Class<?> clazz) {
        Map<String, String> map = new HashMap<>();
        List<Field> fields = getAllFields(clazz);
        fields.forEach(i -> map.put(i.getName(), i.getName()));
        return map;
    }


    public static List<Field> getAllFields(Class<?> clazz) {
        return ReflectionUtil.getFieldList(ClassUtil.getUserClass(clazz));
    }


    /**
     * 获取实体对应字段 MAP
     *
     * @param clazz 实体类
     * @return 缓存 map
     */
    public static Map<String, String> getAllFieldNameMap(Class<?> clazz) {
        return COLUMN_CACHE_MAP.computeIfAbsent(clazz.getName(), key -> createColumnCacheMap(clazz));
    }

}
