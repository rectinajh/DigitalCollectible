package com.nft.authorization.controller;

import com.nft.authorization.service.AuthorizationRealName;
import com.nft.authorization.util.AuthorizationResultBuild;
import com.nft.authorization.vo.DataResult;
import com.nft.common.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

@Controller
@RequestMapping("/realName")
public class AuthorizationController {

    @Autowired
    private AuthorizationRealName authorization;

    @PostMapping("/authorization")
    @ResponseBody
    public Result<DataResult> authorizationRealName(@RequestBody Map<String,String> param) throws NoSuchAlgorithmException, InvalidKeyException, IOException {
        String authorizationStr = authorization.authorizationRealName(param);
        System.out.println(authorizationStr);
        DataResult result = AuthorizationResultBuild.getResult(authorizationStr);
        System.out.println(result);
        return Result.success(AuthorizationResultBuild.getResult(authorizationStr));
    }
}
