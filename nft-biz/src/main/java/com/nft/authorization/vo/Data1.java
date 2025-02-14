package com.nft.authorization.vo;

public class Data1 {
    private Data2 data;
    private String seqNum;
    private String message;
    private Integer status;

    @Override
    public String toString() {
        return "Data1{" +
                "data=" + data +
                ", seqNum='" + seqNum + '\'' +
                ", message='" + message + '\'' +
                ", status=" + status +
                '}';
    }

    public String getSeqNum() {
        return seqNum;
    }

    public void setSeqNum(String seqNum) {
        this.seqNum = seqNum;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }


    public Data2 getData() {
        return data;
    }

    public void setData(Data2 data) {
        this.data = data;
    }
}
