package com.ppxb.latea.starter.core.enums;

import java.io.Serializable;

/**
 * 枚举接口
 *
 * @param <T> value 类型
 */
public interface BaseEnum<T extends Serializable> {

    /**
     * 枚举值
     */
    T getValue();

    /**
     * 枚举描述
     */
    String getDescription();

    /**
     * 颜色
     */
    default String getColor() {
        return null;
    }
}
