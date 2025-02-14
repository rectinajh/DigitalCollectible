package com.nft.common.utils;

import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.sms.v20210111.SmsClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
@Service
public class SendSmsClient {

    private  SmsClient client;

    @Value("${tencent-cloud.sendSmsId}")
    private String secretId;
    @Value("${tencent-cloud.sendSmsKey}")
    private String secretKey;


    public SmsClient getClient() throws IOException {
        if(client == null){
            synchronized (SendSmsClient.class){
                if(client == null){

                    // 实例化一个认证对象，入参需要传入腾讯云账户 SecretId，SecretKey。
                    // 为了保护密钥安全，建议将密钥设置在环境变量中或者配置文件中，请参考凭证管理 https://github.com/TencentCloud/tencentcloud-sdk-java?tab=readme-ov-file#%E5%87%AD%E8%AF%81%E7%AE%A1%E7%90%86。
                    // 硬编码密钥到代码中有可能随代码泄露而暴露，有安全隐患，并不推荐。
                    // SecretId、SecretKey 查询: https://console.cloud.tencent.com/cam/capi
                    // Credential cred = new Credential("SecretId", "SecretKey");
                    //Credential cred = new Credential(System.getenv("TENCENTCLOUD_SECRET_ID"), System.getenv("TENCENTCLOUD_SECRET_KEY"));
                    Credential cred = new Credential(secretId, secretKey);
                    // 实例化一个http选项，可选的，没有特殊需求可以跳过
                    HttpProfile httpProfile = new HttpProfile();
                    // 从3.0.96版本开始, 单独设置 HTTP 代理（无需要直接忽略）
                    // httpProfile.setProxyHost("真实代理ip");
                    // httpProfile.setProxyPort(真实代理端口);
                    httpProfile.setReqMethod("GET"); // get请求(默认为post请求)
                    httpProfile.setConnTimeout(10); // 请求连接超时时间，单位为秒(默认60秒)
                    httpProfile.setWriteTimeout(10);  // 设置写入超时时间，单位为秒(默认0秒)
                    httpProfile.setReadTimeout(10);  // 设置读取超时时间，单位为秒(默认0秒)

                    /**
                     * 指定接入地域域名，默认就近地域接入域名为 sms.tencentcloudapi.com ，
                     *             也支持指定地域域名访问，例如广州地域的域名为 sms.ap-guangzhou.tencentcloudapi.com
                     */
                    httpProfile.setEndpoint("sms.tencentcloudapi.com");
                    /* 非必要步骤:
                     * 实例化一个客户端配置对象，可以指定超时时间等配置 */
                    ClientProfile clientProfile = new ClientProfile();
                    /* SDK默认用TC3-HMAC-SHA256进行签名
                     * 非必要请不要修改这个字段 */
                    clientProfile.setSignMethod("HmacSHA256");
                    clientProfile.setHttpProfile(httpProfile);
                        /* 实例化要请求产品(以sms为例)的client对象
                         * 第二个参数是地域信息，可以直接填写字符串ap-guangzhou，
                         支持的地域列表参考 https://cloud.tencent.com/document/api/382/52071#.E5.9C.B0.E5.9F.9F.E5.88.97.E8.A1.A8
                         */
                     //client = new SmsClient(cred, "ap-shanghai",clientProfile);
                    client = new SmsClient(cred, "ap-guangzhou",clientProfile);
                }
            }
        }
        return client;
    }

}
