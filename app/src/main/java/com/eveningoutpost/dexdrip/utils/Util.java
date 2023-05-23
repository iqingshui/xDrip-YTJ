package com.eveningoutpost.dexdrip.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class Util {

    public static String doGet(String url, String params) {

        BufferedReader in = null;
        try {
            String urlString = url + "?" + params;
            URL realUrl = new URL(urlString);
            System.out.println(urlString);

            // 打开和URL之间的连接
            HttpURLConnection conn = (HttpURLConnection) realUrl.openConnection();

            // 设置请求参数
            conn.setRequestMethod("GET");// 默认GET，可以不填
            conn.setUseCaches(false);
            conn.setConnectTimeout(5000); //请求超时时间

            // 设置请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            // conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");

            // 建立实际的连接
            conn.connect();

            // 获取所有响应头
            Map<String, List<String>> map = conn.getHeaderFields();
            for (String key : map.keySet()) {
                System.out.println(key + "=" + map.get(key));
            }

            // 获取响应
            String line = null;
            StringBuffer sb = new StringBuffer();
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            while ((line = in.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                    in = null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
