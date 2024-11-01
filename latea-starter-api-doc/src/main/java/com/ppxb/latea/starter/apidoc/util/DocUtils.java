package com.ppxb.latea.starter.apidoc.util;

import com.ppxb.latea.starter.core.enums.BaseEnum;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * API文档工具类
 * <p>
 * 该工具类提供了一系列用于处理API文档相关的静态工具方法，主要功能包括：
 * <ul>
 * <li>枚举类型解析和处理</li>
 * <li>Controller注解检查</li>
 * <li>枚举描述信息获取</li>
 * </ul>
 * </p>
 *
 * @author ppxb
 * @since 1.0.0
 */
public class DocUtils {

    private DocUtils() {
    }

    /**
     * 获取枚举值的数据类型字符串表示
     * <p>
     * 通过解析枚举类实现的BaseEnum接口的泛型参数，确定枚举值的具体数据类型。
     * 支持的类型映射关系：
     * <ul>
     * <li>Integer -> "integer"</li>
     * <li>Long -> "long"</li>
     * <li>Double -> "number"</li>
     * <li>String -> "string"</li>
     * </ul>
     * </p>
     *
     * @param enumClass 待解析的枚举类
     * @return 返回枚举值类型的字符串表示，默认返回"string"
     */
    public static String getEnumValueTypeAsString(Class<?> enumClass) {
        // 获取枚举类实现的所有接口
        Type[] interfaces = enumClass.getGenericInterfaces();
        // 定义枚举值类型的映射
        Map<Class<?>, String> typeMap = Map
            .of(Integer.class, "integer", Long.class, "long", Double.class, "number", String.class, "string");
        // 遍历所有接口
        for (Type type : interfaces) {
            // 检查接口是否为参数化类型并且原始类型为 BaseEnum
            if (type instanceof ParameterizedType parameterizedType && parameterizedType
                .getRawType() == BaseEnum.class) {
                Type actualType = parameterizedType.getActualTypeArguments()[0];
                // 检查实际类型参数是否为类类型，并返回对应的字符串类型
                if (actualType instanceof Class<?> actualClass) {
                    return typeMap.getOrDefault(actualClass, "string");
                }
            }
        }
        // 默认返回 "string" 类型
        return "string";
    }

    /**
     * 解析枚举值类型对应的格式
     * <p>
     * 将枚举值类型转换为对应的格式字符串，转换关系：
     * <ul>
     * <li>integer -> int32</li>
     * <li>long -> int64</li>
     * <li>number -> double</li>
     * <li>其他类型 -> 原值返回</li>
     * </ul>
     * </p>
     *
     * @param enumValueType 枚举值类型字符串
     * @return 对应的格式字符串
     */
    public static String resolveFormat(String enumValueType) {
        return switch (enumValueType) {
            case "integer" -> "int32";
            case "long" -> "int64";
            case "number" -> "double";
            default -> enumValueType;
        };
    }

    /**
     * 获取枚举类的值-描述映射关系
     * <p>
     * 将枚举类中所有枚举常量的值和描述信息组装成Map返回。
     * 返回的Map保持枚举定义的顺序（使用LinkedHashMap）。
     * </p>
     *
     * @param enumClass 枚举类Class对象
     * @return 返回一个有序Map，key为枚举值，value为枚举描述
     * @throws ClassCastException 如果提供的类不是BaseEnum的实现类
     */
    public static Map<Object, String> getDescMap(Class<?> enumClass) {
        BaseEnum<?>[] enums = (BaseEnum<?>[])enumClass.getEnumConstants();
        return Arrays.stream(enums)
            .collect(Collectors.toMap(BaseEnum::getValue, BaseEnum::getDescription, (a, b) -> a, LinkedHashMap::new));
    }

    /**
     * 检查类是否具有RestController注解
     * <p>
     * 递归检查类及其父类是否标注了@RestController注解。
     * 该方法会遍历整个类继承层次结构，直到找到带有RestController注解的类或达到继承链顶端。
     * </p>
     *
     * @param clazz 待检查的类
     * @return 如果类或其父类具有RestController注解则返回true，否则返回false
     */
    public static boolean hasRestControllerAnnotation(Class<?> clazz) {
        if (clazz == null || clazz.equals(Object.class)) {
            return false;
        }
        // 检查当前类是否有RestController注解
        if (clazz.isAnnotationPresent(RestController.class)) {
            return true;
        }
        // 递归检查父类
        return hasRestControllerAnnotation(clazz.getSuperclass());
    }
}
