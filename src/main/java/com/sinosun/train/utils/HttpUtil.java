package com.sinosun.train.utils;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson.JSONObject;
import com.sinosun.train.constants.RedisKeyConstant;
import com.sinosun.train.enums.PlatformErrorCode;
import com.sinosun.train.exception.ServiceException;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Created on 2019/1/15 19:46.
 *
 * @author caogu
 */
public class HttpUtil {
    private static final Logger logger = LoggerFactory.getLogger(HttpUtil.class);
    private static final int TIMEOUT = (int) TimeUnit.SECONDS.toMillis(50);

    /**
     * 获取cookie的试登录链接
     */
    private static final String getCookieUrl = "https://kyfw.12306.cn/otn/leftTicket/init?linktypeid=dc&fs=%E7%A6%8F%E5%B7%9E,FZS&ts=%E7%A6%8F%E5%B7%9E%E5%8D%97,FYS&date={}&flag=N,N,Y";

    /**
     * 执行HTTP请求  <p>get请求时data传null</p>
     *
     * @param url    url完整地址
     * @param method Connection.Method.GET  Connection.Method.POST
     * @param data   请求参数的JSON对象
     * @return HTTP接口返回值
     */
    public static String request(String url, Connection.Method method, JSONObject data) {
        logger.info("======request url={} data={}", url, data);
        String result;
        try {
            Connection conn = Jsoup.connect(url)
                    .timeout(TIMEOUT)
                    .header("Host", "kyfw.12306.cn")
                    .header("Accept", "application/json")
                    .header("Content-Type", "application/json")
                    .header("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.6,en;q=0.4,de-DE;q=0.2")
                    .header("Accept-Encoding", "gzip, deflate, br")
                    .header("Connection", "keep-alive")
                    .header("Upgrade-Insecure-Requests", "1")
                    .header("Pragma", "no-cache")
                    .header("Cache-Control", "no-cache")
                    .header("Cookie", getCookie())
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:70.0) Gecko/20100101 Firefox/70.0")
                    .followRedirects(true)
                    .ignoreContentType(true);

            if (data != null) {
                conn.requestBody(data.toJSONString());
            }
            Connection.Response response = conn.method(method).execute();

            int code = response.statusCode();
            if (code == HttpStatus.OK.value() || code == HttpStatus.FOUND.value()) {
                result = response.body();
            } else {
                logger.error("执行url={}返回非200/302值, statusCode={}", url, response.statusCode());
                throw new ServiceException(PlatformErrorCode.SERVICE_INTERNAL_ERROR);
            }
            logger.info("======request code={} result={}", code, result);
        } catch (IOException e) {
            logger.error("执行" + url + "出错, data=" + data, e);
            throw new ServiceException(PlatformErrorCode.SERVICE_INTERNAL_ERROR, e);
        }
        return result;
    }

    /**
     * 获取cookie
     *
     * @return
     */
    public static String getCookie() {
        // redis中存在cookie，则使用redis的cookie
        String cookie = RedisUtils.getString(RedisKeyConstant.REDIS_KEY_LOCAL_DATA_COOKIE_12306);
        if (StrUtil.isNotBlank(cookie)) {
            return cookie;
        }

        String reqUrl = StrUtil.format(getCookieUrl, DateUtil.today());
        HttpResponse response = cn.hutool.http.HttpUtil.createGet(reqUrl)
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:70.0) Gecko/20100101 Firefox/70.0")
                .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                .header("Host", "kyfw.12306.cn")
                .execute();

        String result = replaceAll(Convert.toStr(response.headerList("Set-Cookie")), "[", "]", "path=/", "Path=/otn", ", ");
        RedisUtils.setString(RedisKeyConstant.REDIS_KEY_LOCAL_DATA_COOKIE_12306, result, 3L, TimeUnit.DAYS);
        logger.info("======> 请求url = {}，cookie = {}", reqUrl, result);
        return result;
    }

    /**
     * 删除字符串中指定的字符
     *
     * @param str
     * @param param
     * @return
     */
    private static String replaceAll(String str, String... param) {
        if (param.length <= 0) {
            return null;
        }

        String result = str;
        for (String r : param) {
            result = StrUtil.replaceIgnoreCase(result, r, "");
        }

        return result;
    }

}
