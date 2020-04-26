package com.xinsite.common.uitls.network;

import com.xinsite.common.uitls.Global;
import com.xinsite.common.uitls.json.JSON;
import com.xinsite.common.uitls.json.JSONObject;
import com.xinsite.common.uitls.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 获取地址类
 */
public class AddressUtils {
    private static final Logger log = LoggerFactory.getLogger(AddressUtils.class);

    public static String getRealAddressByIP(String ip) {
        if (IpUtils.isLocalAddr(ip)) {
            return "内网IP";
        }
        String address;
        if (Global.getBoolean("config.is_enabled")) {
            address = AddressUtils.getAddressByTaoBao(ip);
        } else {
            return "XX XX";
        }
        if (StringUtils.isEmpty(address)) return "无法获知";
        return address;
    }

    /**
     * 地址查询：淘宝API接口
     */
    public static String getAddressByTaoBao(String ip) {
        String address = StringUtils.EMPTY;
        String rspStr = HttpUtils.sendPost("http://ip.taobao.com/service/getIpInfo.php?ip=" + ip);
        if (StringUtils.isNotEmpty(rspStr)) {
            try {
                JSONObject obj = JSON.unmarshal(rspStr, JSONObject.class);
                if (obj.getInt("code") == 0) {
                    JSONObject data = obj.getObj("data");
                    String region = data.getStr("region");
                    if (StringUtils.isEmpty(region)) region = "XX";
                    String city = data.getStr("city");
                    if (region.equals(city)) address = region;
                    else address = region + " " + city;
                }
            } catch (Exception e) {
                log.error("获取地理位置异常 {}", ip);
            }
        }
        return address;
    }
}
