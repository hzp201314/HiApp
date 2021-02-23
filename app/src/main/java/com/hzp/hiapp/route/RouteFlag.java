package com.hzp.hiapp.route;

/**
 * 为目标页扩展属性
 */
public interface RouteFlag {
    /**
     * 登陆：0x01
     */
    int FLAG_LOGIN = 0x01;
    /**
     * 实名认证：0x02
     */
    int FLAG_AUTHENTICATION = FLAG_LOGIN << 1;
    /**
     * 成为会员：0x03
     */
    int FLAG_VIP = FLAG_AUTHENTICATION << 1;
    /**
     * 按需扩展
     */
}
