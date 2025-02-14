package com.nft.authorization.vo;

public class DataResult {
    private Integer code;
    private String msg;
    private String serialNumber;
    private Data1 data;


    @Override
    public String toString() {
        return "DataResult{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", serialNumber='" + serialNumber + '\'' +
                ", data=" + data +
                '}';
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public Data1 getData() {
        return data;
    }

    public void setData(Data1 data) {
        this.data = data;
    }


}
