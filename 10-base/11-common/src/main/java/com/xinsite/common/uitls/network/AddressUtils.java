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

    public static final String IP_URL = "http://ip.taobao.com/service/getIpInfo.php";

    public static String getRealAddressByIP(String ip) {
        String address = "无法获知";

        // 内网不查询
        if (IpUtils.isLocalAddr(ip)) {
            return "内网IP";
        }
        if (Global.getBoolean("config.is_enabled")) {
            String rspStr = HttpUtils.sendPost(IP_URL, "ip=" + ip);
            if (StringUtils.isEmpty(rspStr)) {
                //log.error("获取地理位置异常 {}", ip);
                return address;
            }
            JSONObject obj;
            try {
                obj = JSON.unmarshal(rspStr, JSONObject.class);
                JSONObject data = obj.getObj("data");
                String region = data.getStr("region");
                String city = data.getStr("city");
                address = region + " " + city;
                if (region.equals(city)) address = region;
            } catch (Exception e) {
                log.error("获取地理位置异常 {}", ip);
            }
        }
        return address;
    }
}
