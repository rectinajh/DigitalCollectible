package com.nft.authorization.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.nft.authorization.vo.Data1;
import com.nft.authorization.vo.Data2;
import com.nft.authorization.vo.DataResult;

public class AuthorizationResultBuild {

    public static DataResult getResult(String jsonStr){
        JSONObject jsonObject = JSON.parseObject(jsonStr);
        DataResult dataResult = new DataResult();
        Data1 data1 = new Data1();
        Data2 data2 = new Data2();
        if(jsonObject.getInteger("code") == 200){
            dataResult.setCode(jsonObject.getInteger("code"));
            dataResult.setMsg(jsonObject.getString("msg"));
            dataResult.setSerialNumber(jsonObject.getString("serialNumber"));
            String data = jsonObject.getString("data");
            JSONObject jsonObject1 = JSON.parseObject(data);
            data1.setSeqNum(jsonObject1.getString("seqNum"));
            data1.setMessage(jsonObject1.getString("message"));
            data1.setStatus(jsonObject1.getInteger("status"));
            String data3 = jsonObject1.getString("data");
            JSONObject jsonObject2 = JSON.parseObject(data3);
            data2.setResult(jsonObject2.getInteger("result"));
            data2.setResultMsg(jsonObject2.getString("resultMsg"));
            data1.setData(data2);
            dataResult.setData(data1);
            return dataResult;
        }else {
            dataResult.setCode(jsonObject.getInteger("code"));
            dataResult.setMsg(jsonObject.getString("msg"));
            dataResult.setSerialNumber(jsonObject.getString("serialNumber"));
            dataResult.setData(null);
            return dataResult;
        }


    }
}
