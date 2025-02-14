package com.nft.business;

public enum ErrorCode implements IErrorCode{
    /**
     * System error
     */
    SUCCESS(0, "SUCCESS"),
    /**
     * The Sdk invalid parameter.
     */
    NFT_INVALID_PARAMETER(30000, "nft parameter is invalid"),
    /**
     * The Sdk get file input stream failed.
     */
    NFT_GET_FILE_INPUT_STREAM_FAILED(30004, "nft get file input stream failed"),
    ;
    private int value;

    private String desc;

    ErrorCode(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public static IErrorCode valueOf(int value) {
        return forNumber(value);
    }

    public static IErrorCode forNumber(final int value) {
        for (ErrorCode e : ErrorCode.values()) {
            if (e.getErrorCode() == value) {
                return e;
            }
        }
        return new IErrorCode() {
            @Override
            public int getErrorCode() {
                return value;
            }

            @Override
            public String getErrorDesc() {
                return "NFT_ERROR_CODE";
            }

            @Override
            public boolean isSuccess() {
                return false;
            }

            @Override
            public String toString() {
                return "ErrorCode{" +
                        "value=" + value +
                        ", desc='" + "NFT_ERROR_CODE" + '\'' +
                        '}';
            }
        };
    }

    public String getDesc() {
        return desc;
    }

    @Override
    public int getErrorCode() {
        return value;
    }

    @Override
    public String getErrorDesc() {
        return String.valueOf(desc);
    }

    @Override
    public boolean isSuccess() {
        return this == ErrorCode.SUCCESS;
    }

    @Override
    public String toString() {
        return "ErrorCode{" +
                "value=" + value +
                ", desc='" + desc + '\'' +
                '}';
    }
}
