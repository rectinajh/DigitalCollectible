package com.nft.authorization.vo;

public class Data2 {
    private Integer result;
    private String resultMsg;

    @Override
    public String toString() {
        return "Data2{" +
                "result=" + result +
                ", resultMsg='" + resultMsg + '\'' +
                '}';
    }

    public Integer getResult() {
        return result;
    }

    public void setResult(Integer result) {
        this.result = result;
    }

    public String getResultMsg() {
        return resultMsg;
    }

    public void setResultMsg(String resultMsg) {
        this.resultMsg = resultMsg;
    }
}
