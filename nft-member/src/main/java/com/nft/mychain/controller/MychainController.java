package com.nft.mychain.controller;

import com.nft.common.vo.Result;
import com.nft.myChain.service.MyChainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Controller
@RequestMapping("/mychain")
public class MychainController {
    @Autowired
    private MyChainService myChainService;

    /**
     * 蚂蚁链上链操作
     * @param data
     * @return
     */
    @PostMapping("/upper-chain")
    @ResponseBody
    public Result<String> mychainUpper(String data) throws IOException {
        return Result.success(myChainService.depositdata(data));
    }

    /**
     * 蚂蚁链上链操作
     * @param txHash
     * @return
     */
    @GetMapping("/query-transaction")
    @ResponseBody
    public Result<String> mychainQueryTransaction(@RequestParam("txHash") String txHash) throws IOException {
        return Result.success(myChainService.queryTransaction(txHash));
    }
}
