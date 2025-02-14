package com.nft.sms.controller;

import com.nft.common.vo.Result;
import com.nft.sms.service.SendSms;
import com.tencentcloudapi.sms.v20210111.models.SendSmsResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/sendSms")
public class SendSmsController {
    @Autowired
    private SendSms sendSms;

    @GetMapping("/getCode")
    @ResponseBody
    public Result<SendSmsResponse> sendSmsGetVerificationCode(String phoneNum) {
        return Result.success(sendSms.sendSmsByTenCentCloud(phoneNum));
    }
}
