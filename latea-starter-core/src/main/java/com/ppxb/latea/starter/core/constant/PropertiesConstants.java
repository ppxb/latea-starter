package com.ppxb.latea.starter.core.constant;

/**
 * 配置属性相关常量
 */
public class PropertiesConstants {

    /**
     * Latea Starter
     */
    public static final String LATEA_STARTER = "latea-starter";

    /**
     * 启用配置
     */
    public static final String ENABLED = "enabled";

    /**
     * 安全配置
     */
    public static final String SECURITY = LATEA_STARTER + StringConstants.DOT + "security";

    /**
     * 密码编解码配置
     */
    public static final String SECURITY_PASSWORD = SECURITY + StringConstants.DOT + "password";

    /**
     * 加/解密配置
     */
    public static final String SECURITY_CRYPTO = SECURITY + StringConstants.DOT + "crypto";

    /**
     * 限流器配置
     */
    public static final String SECURITY_LIMITER = SECURITY + StringConstants.DOT + "limiter";

    /**
     * Web 配置
     */
    public static final String WEB = LATEA_STARTER + StringConstants.DOT + "web";

    /**
     * 跨域配置
     */
    public static final String WEB_CORS = WEB + StringConstants.DOT + "cors";

    /**
     * 响应配置
     */
    public static final String WEB_RESPONSE = WEB + StringConstants.DOT + "response";

    /**
     * 链路配置
     */
    public static final String WEB_TRACE = WEB + StringConstants.DOT + "trace";

    /**
     * XSS 配置
     */
    public static final String WEB_XSS = WEB + StringConstants.DOT + "xss";

    /**
     * 日志配置
     */
    public static final String LOG = LATEA_STARTER + StringConstants.DOT + "log";

    /**
     * 存储配置
     */
    public static final String STORAGE = LATEA_STARTER + StringConstants.DOT + "storage";

    /**
     * 本地存储配置
     */
    public static final String STORAGE_LOCAL = STORAGE + StringConstants.DOT + "local";

    /**
     * 验证码配置
     */
    public static final String CAPTCHA = LATEA_STARTER + StringConstants.DOT + "captcha";

    /**
     * 图形验证码配置
     */
    public static final String CAPTCHA_GRAPHIC = CAPTCHA + StringConstants.DOT + "graphic";

    /**
     * 行为验证码配置
     */
    public static final String CAPTCHA_BEHAVIOR = CAPTCHA + StringConstants.DOT + "behavior";

    /**
     * 消息配置
     */
    public static final String MESSAGING = LATEA_STARTER + StringConstants.DOT + "messaging";

    /**
     * WebSocket 配置
     */
    public static final String MESSAGING_WEBSOCKET = MESSAGING + StringConstants.DOT + "websocket";

    /**
     * CRUD 配置
     */
    public static final String CRUD = LATEA_STARTER + StringConstants.DOT + "crud";

    /**
     * 数据权限配置
     */
    public static final String DATA_PERMISSION = LATEA_STARTER + StringConstants.DOT + "data-permission";

    /**
     * 多租户配置
     */
    public static final String TENANT = LATEA_STARTER + StringConstants.DOT + "tenant";

    private PropertiesConstants() {
    }
}
