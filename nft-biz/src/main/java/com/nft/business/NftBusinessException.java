package com.nft.business;

public class NftBusinessException extends RuntimeException {
    private final IErrorCode errorCode;
    private final String errorMessage;

    public NftBusinessException(IErrorCode errorCode, String errorMessage, Throwable e) {
        super(errorMessage, e);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public NftBusinessException(IErrorCode errorCode, String errorMessage) {
        super(errorMessage);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public NftBusinessException(IErrorCode errorCode) {
        super(errorCode.getErrorDesc());
        this.errorCode = errorCode;
        this.errorMessage = errorCode.getErrorDesc();
    }

    /**
     * Throw invalid parameters.
     *
     * @param msg the msg
     */
    public static void throwInvalidParameters(String msg) {
        throw new NftBusinessException(ErrorCode.NFT_INVALID_PARAMETER, msg);
    }

    public static void throwInvalidParameters() {
        throw new NftBusinessException(ErrorCode.NFT_INVALID_PARAMETER, "parameters should be valid");
    }

    /**
     * Throw null parameters.
     */
    public static void throwNullParameters() {
        throw new NftBusinessException(ErrorCode.NFT_INVALID_PARAMETER, "parameters should not be null");
    }

    public IErrorCode getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
