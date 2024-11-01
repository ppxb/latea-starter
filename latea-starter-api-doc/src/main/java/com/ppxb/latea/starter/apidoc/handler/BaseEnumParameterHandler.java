package com.ppxb.latea.starter.apidoc.handler;

import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.SimpleType;
import com.ppxb.latea.starter.apidoc.util.DocUtils;
import com.ppxb.latea.starter.core.enums.BaseEnum;
import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;
import org.springdoc.core.customizers.ParameterCustomizer;
import org.springdoc.core.customizers.PropertyCustomizer;
import org.springframework.core.MethodParameter;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

/**
 * 自定义 BaseEnum 枚举参数处理器
 * <p>
 * 该处理器主要用于处理实现了 BaseEnum 接口的枚举类型，主要功能包括：
 * <ul>
 * <li>自定义枚举参数的展示格式</li>
 * <li>优化枚举值和描述的展示方式</li>
 * <li>处理枚举类型的参数和属性定制化</li>
 * </ul>
 * </p>
 *
 * @author ppxb
 * @since 1.0.0
 */
public class BaseEnumParameterHandler implements ParameterCustomizer, PropertyCustomizer {

    /**
     * 自定义参数处理方法
     * <p>
     * 处理实现了 BaseEnum 接口的方法参数，优化其在 Swagger 文档中的展示。
     * 主要处理逻辑包括：
     * <ul>
     * <li>判断参数是否为 BaseEnum 类型</li>
     * <li>配置参数的 Schema 信息</li>
     * <li>添加枚举值描述信息</li>
     * </ul>
     * </p>
     *
     * @param parameterModel  参数模型
     * @param methodParameter 方法参数
     * @return 处理后的参数对象
     */
    @Override
    public Parameter customize(Parameter parameterModel, MethodParameter methodParameter) {
        Class<?> parameterType = methodParameter.getParameterType();
        // 判断是否为 BaseEnum 的子类型
        if (!ClassUtil.isAssignable(BaseEnum.class, parameterType)) {
            return parameterModel;
        }

        String description = parameterModel.getDescription();
        if (StrUtil.contains(description, "color:red")) {
            return parameterModel;
        }

        // 自定义枚举描述并封装参数配置
        configureSchema(parameterModel.getSchema(), parameterType);
        parameterModel.setDescription(appendEnumDescription(description, parameterType));
        return parameterModel;
    }

    /**
     * 自定义属性处理方法
     * <p>
     * 处理实现了 BaseEnum 接口的类属性，优化其在 Swagger 文档中的展示。
     * 主要处理逻辑包括：
     * <ul>
     * <li>解析属性的实际类型</li>
     * <li>配置属性的 Schema 信息</li>
     * <li>添加枚举值描述信息</li>
     * </ul>
     * </p>
     *
     * @param schema Schema 对象
     * @param type   注解类型
     * @return 处理后的 Schema 对象
     */
    @Override
    public Schema customize(Schema schema, AnnotatedType type) {
        Class<?> rawClass = resolveRawClass(type.getType());
        // 判断是否为 BaseEnum 的子类型
        if (!ClassUtil.isAssignable(BaseEnum.class, rawClass)) {
            return schema;
        }

        // 自定义参数描述并封装参数配置
        configureSchema(schema, rawClass);
        schema.setDescription(appendEnumDescription(schema.getDescription(), rawClass));
        return schema;
    }

    /**
     * 配置 Schema 信息
     * <p>
     * 设置枚举类的 Schema 配置，包括：
     * <ul>
     * <li>设置可选值列表</li>
     * <li>设置数据类型</li>
     * <li>设置格式化方式</li>
     * </ul>
     * </p>
     *
     * @param schema    Schema 对象
     * @param enumClass 枚举类
     */
    private void configureSchema(Schema schema, Class<?> enumClass) {
        BaseEnum[] enums = (BaseEnum[]) enumClass.getEnumConstants();
        List<String> valueList = Arrays.stream(enums).map(e -> e.getValue().toString()).toList();

        schema.setEnum(valueList);
        String enumValueType = DocUtils.getEnumValueTypeAsString(enumClass);
        schema.setType(enumValueType);
        schema.setFormat(DocUtils.resolveFormat(enumValueType));
    }

    /**
     * 追加枚举描述信息
     * <p>
     * 在原有描述的基础上，添加带有样式的枚举值-描述映射信息
     * </p>
     *
     * @param originalDescription 原始描述
     * @param enumClass           枚举类
     * @return 添加了枚举描述的完整描述信息
     */
    private String appendEnumDescription(String originalDescription, Class<?> enumClass) {
        return originalDescription + "<span style='color:red'>" + DocUtils.getDescMap(enumClass) + "</span>";
    }

    /**
     * 解析类型的原始类
     * <p>
     * 支持以下类型的解析：
     * <ul>
     * <li>SimpleType - 直接返回原始类</li>
     * <li>CollectionType - 返回集合元素类型</li>
     * <li>其他类型 - 返回 Object.class</li>
     * </ul>
     * </p>
     *
     * @param type 需要解析的类型
     * @return 解析后的原始类
     */
    private Class<?> resolveRawClass(Type type) {
        if (type instanceof SimpleType simpleType) {
            return simpleType.getRawClass();
        } else if (type instanceof CollectionType collectionType) {
            return collectionType.getContentType().getRawClass();
        } else {
            return Object.class;
        }
    }
}
