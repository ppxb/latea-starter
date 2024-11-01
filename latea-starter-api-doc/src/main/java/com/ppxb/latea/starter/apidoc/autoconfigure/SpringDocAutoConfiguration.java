package com.ppxb.latea.starter.apidoc.autoconfigure;

import cn.hutool.core.map.MapUtil;
import com.ppxb.latea.starter.apidoc.handler.BaseEnumParameterHandler;
import com.ppxb.latea.starter.apidoc.handler.OpenApiHandler;
import com.ppxb.latea.starter.core.autoconfigure.project.ProjectProperties;
import com.ppxb.latea.starter.core.util.GeneralPropertySourceFactory;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springdoc.core.configuration.SpringDocConfiguration;
import org.springdoc.core.customizers.GlobalOpenApiCustomizer;
import org.springdoc.core.customizers.OpenApiBuilderCustomizer;
import org.springdoc.core.customizers.ServerBaseUrlCustomizer;
import org.springdoc.core.properties.SpringDocConfigProperties;
import org.springdoc.core.providers.JavadocProvider;
import org.springdoc.core.service.OpenAPIService;
import org.springdoc.core.service.SecurityService;
import org.springdoc.core.utils.PropertyResolverUtils;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * SpringDoc API文档自动配置类
 * <p>
 * 该配置类提供了 API 文档相关的自动配置功能，主要包括：
 * <ul>
 * <li>配置 Swagger UI 资源处理</li>
 * <li>自定义 OpenAPI 文档信息</li>
 * <li>配置全局安全认证</li>
 * <li>自定义枚举处理</li>
 * </ul>
 * </p>
 *
 * <p>
 * 主要功能：
 * <ul>
 * <li>自动配置 API 文档的基础信息（标题、版本、描述等）</li>
 * <li>支持自定义安全认证配置</li>
 * <li>提供静态资源访问配置</li>
 * <li>支持枚举值的智能解析和展示</li>
 * </ul>
 * </p>
 *
 * @author ppxb
 * @see SpringDocConfiguration
 * @see WebMvcConfigurer
 * @since 1.0.0
 */
@EnableWebMvc
@AutoConfiguration(before = SpringDocConfiguration.class)
@EnableConfigurationProperties(SpringDocExtensionProperties.class)
@PropertySource(value = "classpath:default-api-doc.yml", factory = GeneralPropertySourceFactory.class)
public class SpringDocAutoConfiguration implements WebMvcConfigurer {

    private static final Logger log = LoggerFactory.getLogger(SpringDocAutoConfiguration.class);

    /**
     * 配置静态资源处理器
     * <p>
     * 配置 Swagger UI 相关的静态资源访问路径，包括：
     * <ul>
     * <li>favicon.ico - 网站图标</li>
     * <li>doc.html - API 文档页面</li>
     * <li>webjars - API 文档相关的静态资源</li>
     * </ul>
     * </p>
     *
     * @param registry 资源处理器注册表
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/favicon.ico").addResourceLocations("classpath:/");
        registry.addResourceHandler("/doc.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**")
            .addResourceLocations("classpath:/META-INF/resources/webjars/")
            .setCacheControl(CacheControl.maxAge(5, TimeUnit.HOURS).cachePublic());
    }

    /**
     * 配置 OpenAPI 对象
     * <p>
     * 根据项目配置和扩展配置创建 OpenAPI 实例，包含：
     * <ul>
     * <li>基础信息配置（标题、版本、描述）</li>
     * <li>联系人信息配置</li>
     * <li>许可证信息配置</li>
     * <li>安全认证配置</li>
     * </ul>
     * </p>
     *
     * @param projectProperties 项目配置属性
     * @param properties        API文档扩展配置属性
     * @return 配置完成的 OpenAPI 实例
     */
    @Bean
    @ConditionalOnMissingBean
    public OpenAPI openApi(ProjectProperties projectProperties, SpringDocExtensionProperties properties) {
        Info info = new Info().title("%s %s".formatted(projectProperties.getName(), "API 文档"))
            .version(projectProperties.getVersion())
            .description(projectProperties.getDescription());
        ProjectProperties.Contact contact = projectProperties.getContact();
        if (null != contact) {
            info.contact(new Contact().name(contact.getName()).email(contact.getEmail()).url(contact.getUrl()));
        }
        ProjectProperties.License license = projectProperties.getLicense();
        if (null != license) {
            info.license(new License().name(license.getName()).url(license.getUrl()));
        }
        OpenAPI openApi = new OpenAPI();
        openApi.info(info);
        Components components = properties.getComponents();
        if (null != components) {
            openApi.components(components);
            // 鉴权配置
            Map<String, SecurityScheme> securitySchemeMap = components.getSecuritySchemes();
            if (MapUtil.isNotEmpty(securitySchemeMap)) {
                SecurityRequirement securityRequirement = new SecurityRequirement();
                List<String> list = securitySchemeMap.values().stream().map(SecurityScheme::getName).toList();
                list.forEach(securityRequirement::addList);
                openApi.addSecurityItem(securityRequirement);
            }
        }
        return openApi;
    }

    /**
     * 配置全局 OpenAPI 自定义器
     * <p>
     * 为所有 API 接口统一配置安全认证要求
     * </p>
     *
     * @param properties API文档扩展配置属性
     * @return 全局 OpenAPI 自定义器
     */
    @Bean
    public GlobalOpenApiCustomizer globalOpenApiCustomizer(SpringDocExtensionProperties properties) {
        return openApi -> {
            if (null != openApi.getPaths()) {
                openApi.getPaths().forEach((s, pathItem) -> {
                    // 为所有接口添加鉴权
                    Components components = properties.getComponents();
                    if (null != components && MapUtil.isNotEmpty(components.getSecuritySchemes())) {
                        Map<String, SecurityScheme> securitySchemeMap = components.getSecuritySchemes();
                        pathItem.readOperations().forEach(operation -> {
                            SecurityRequirement securityRequirement = new SecurityRequirement();
                            List<String> list = securitySchemeMap.values()
                                .stream()
                                .map(SecurityScheme::getName)
                                .toList();
                            list.forEach(securityRequirement::addList);
                            operation.addSecurityItem(securityRequirement);
                        });
                    }
                });
            }
        };
    }

    /**
     * 配置自定义 OpenAPI 构建器
     * <p>
     * 提供自定义的 OpenAPI 文档构建逻辑，支持：
     * <ul>
     * <li>自定义文档信息处理</li>
     * <li>安全配置处理</li>
     * <li>服务器 URL 配置</li>
     * </ul>
     * </p>
     *
     * @param openAPI                   OpenAPI 配置
     * @param securityParser            安全配置解析器
     * @param springDocConfigProperties SpringDoc 配置属性
     * @param propertyResolverUtils     属性解析工具
     * @param openApiBuilderCustomizers OpenAPI 构建器自定义器列表
     * @param serverBaseUrlCustomizers  服务器 URL 自定义器列表
     * @param javadocProvider           Javadoc 提供者
     * @return OpenAPI 服务实例
     */
    @Bean
    public OpenAPIService openApiBuilder(Optional<OpenAPI> openAPI,
                                         SecurityService securityParser,
                                         SpringDocConfigProperties springDocConfigProperties,
                                         PropertyResolverUtils propertyResolverUtils,
                                         Optional<List<OpenApiBuilderCustomizer>> openApiBuilderCustomizers,
                                         Optional<List<ServerBaseUrlCustomizer>> serverBaseUrlCustomizers,
                                         Optional<JavadocProvider> javadocProvider) {
        return new OpenApiHandler(openAPI, securityParser, springDocConfigProperties, propertyResolverUtils, openApiBuilderCustomizers, serverBaseUrlCustomizers, javadocProvider);
    }

    /**
     * 配置枚举参数处理器
     * <p>
     * 用于处理实现了 BaseEnum 接口的枚举类型，优化其在 API 文档中的展示
     * </p>
     *
     * @return BaseEnum 参数处理器实例
     */
    @Bean
    public BaseEnumParameterHandler customParameterCustomizer() {
        return new BaseEnumParameterHandler();
    }

    @PostConstruct
    public void postConstruct() {
        log.debug("[Latea Starter] - Auto Configuration 'ApiDoc' completed initialization.");
    }
}
