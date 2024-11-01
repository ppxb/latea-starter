package com.ppxb.latea.starter.core.util;

import cn.hutool.core.text.CharSequenceUtil;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.DefaultPropertySourceFactory;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.lang.Nullable;

import java.io.IOException;

/**
 * 通用配置文件读取工厂类
 *
 * <p>该类继承自 {@link DefaultPropertySourceFactory}，扩展了对YAML配置文件的支持。
 * 默认的Spring Boot {@code DefaultPropertySourceFactory} 仅支持 .properties 文件，
 * 本实现增加了对 .yml 和 .yaml 文件的加载支持。</p>
 *
 * <p>关于Spring Boot中YAML格式的限制，请参考：
 * <a
 * href="https://docs.spring.io/spring-boot/docs/2.0.6.RELEASE/reference/html/boot-features-external-config.html#boot-features-external-config-yaml-shortcomings">YAML使用限制说明</a></p>
 *
 * @author ppxb
 * @since 1.0.0
 */
public class GeneralPropertySourceFactory extends DefaultPropertySourceFactory {

    /**
     * 创建配置源对象
     *
     * <p>该方法扩展了默认实现，增加了对YAML文件的支持。
     * 如果资源文件名以 '.yml' 或 '.yaml' 结尾，将使用 {@link YamlPropertySourceLoader} 进行处理；
     * 否则，将委托给父类实现来处理 properties 文件。</p>
     *
     * @param name            配置源的名称（可以为 {@code null}）
     * @param encodedResource 要加载配置的编码资源
     * @return 创建的 {@link PropertySource} 实例
     * @throws IOException 如果加载配置源失败会抛出异常
     */
    @Override
    public PropertySource<?> createPropertySource(@Nullable String name,
                                                  EncodedResource encodedResource) throws IOException {
        Resource resource = encodedResource.getResource();
        String resourceName = resource.getFilename();
        if (CharSequenceUtil.isNotBlank(resourceName) && CharSequenceUtil.endWithAny(resourceName, ".yml", ".yaml")) {
            return new YamlPropertySourceLoader().load(resourceName, resource).get(0);
        }
        return super.createPropertySource(name, encodedResource);
    }
}
