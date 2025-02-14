package com.nft.authorization.service;

import com.nft.authorization.util.AuthorizationUtil;
import com.nft.common.utils.IOUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;
@Service
public class AuthorizationRealName {
    @Value("${tencent-cloud.authorizationId}")
    private String secretId;
    @Value("${tencent-cloud.authorizationKey}")
    private String secretKey;

    /**
     * @param param  {"idcard":"身份证"  "name":"姓名"}
     * @return
     * @throws IOException
     * @throws InvalidKeyException
     * @throws NoSuchAlgorithmException
     */
    public String authorizationRealName(Map<String, String> param) throws IOException, InvalidKeyException, NoSuchAlgorithmException {
        String source = "market";
        String result = "";

        Calendar cd = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss 'GMT'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        String datetime = sdf.format(cd.getTime());
        // 签名
        String auth = AuthorizationUtil.calcAuthorization(source, secretId, secretKey, datetime);
        // 请求方法
        String method = "POST";
        // 请求头
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("X-Source", source);
        headers.put("X-Date", datetime);
        headers.put("Authorization", auth);

        // 查询参数
        Map<String, String> queryParams = new HashMap<String, String>();

        // body参数

        // url参数拼接
        String url = "https://service-ngxzc2ub-1310072863.sh.apigw.tencentcs.com/release/web/interface/grsfyz";
        if (!queryParams.isEmpty()) {
            url += "?" + AuthorizationUtil.urlencode(queryParams);
        }

        BufferedReader in = null;
        try {
            URL realUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) realUrl.openConnection();
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            conn.setRequestMethod(method);

            // request headers
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                conn.setRequestProperty(entry.getKey(), entry.getValue());
            }

            // request body
            Map<String, Boolean> methods = new HashMap<>();
            methods.put("POST", true);
            methods.put("PUT", true);
            methods.put("PATCH", true);
            Boolean hasBody = methods.get(method);
            if (hasBody != null) {
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                conn.setDoOutput(true);
                DataOutputStream out = new DataOutputStream(conn.getOutputStream());
                out.writeBytes(AuthorizationUtil.urlencode(param));
                out.flush();
                out.close();
            }

            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;

            while ((line = in.readLine()) != null) {
                result += line;
            }

            System.out.println(result);
        } catch (Exception e) {
            System.out.println(e);
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return result;
    }
}
