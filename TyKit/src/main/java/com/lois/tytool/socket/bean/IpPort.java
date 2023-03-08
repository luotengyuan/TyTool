package com.lois.tytool.socket.bean;

import com.lois.tytool.base.string.RegexUtils;

import java.util.Objects;

/**
 * IP端口信息
 * @author Administrator
 */
public class IpPort {
    private String ip;
    private int port;

    public IpPort(String ip, int port) {
        this.ip = ip;
        this.port = port;
        check();
    }

    private void check() {
        if (!RegexUtils.isIP(ip)) {
            throw new IllegalArgumentException("Ip格式不正确");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        IpPort that = (IpPort) o;

        if (port != that.port) {
            return false;
        }
        return Objects.equals(ip, that.ip);

    }

    @Override
    public int hashCode() {
        int result = ip != null ? ip.hashCode() : 0;
        result = 31 * result + port;
        return result;
    }

    @Override
    public String toString() {
        return "TargetInfo{" +
                "ip='" + ip + '\'' +
                ", port='" + port + '\'' +
                '}';
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
