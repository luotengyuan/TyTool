package com.lois.tytool.basej.generator;

/**
 * JdbcConfig
 * @Author Luo.T.Y
 * @Date 2022/2/22
 * @Time 20:50
 */
public class JdbcConfig {
    /**
     * 驱动类
     */
    private String driverClass;
    /**
     * 数据库URL
     */
    private String connectionUrl;
    /**
     * 用户名
     */
    private String userId;
    /**
     * 密码
     */
    private String password;


    /**
     * 获取 驱动类
     * @return 驱动类
     */
    public String getDriverClass() {
        return this.driverClass;
    }

    /**
     * 设置 驱动类
     * @param driverClass 驱动类
     */
    public void setDriverClass(String driverClass) {
        this.driverClass = driverClass;
    }

    /**
     * 获取 数据库URL
     * @return 数据库URL
     */
    public String getConnectionUrl() {
        return this.connectionUrl;
    }

    /**
     * 设置 数据库URL
     * @param connectionUrl 数据库URL
     */
    public void setConnectionUrl(String connectionUrl) {
        this.connectionUrl = connectionUrl;
    }

    /**
     * 获取 用户名
     * @return 用户名
     */
    public String getUserId() {
        return this.userId;
    }

    /**
     * 设置 用户名
     * @param userId 用户名
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * 获取 密码
     * @return 密码
     */
    public String getPassword() {
        return this.password;
    }

    /**
     * 设置 密码
     * @param password 密码
     */
    public void setPassword(String password) {
        this.password = password;
    }
}
