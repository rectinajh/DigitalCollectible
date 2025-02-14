package com.nft.business;

public interface IErrorCode {
    /**
     * Gets error code.
     *
     * @return the error code
     */
    int getErrorCode();

    /**
     * Gets error desc.
     *
     * @return the error desc
     */
    String getErrorDesc();

    /**
     * Is success boolean.
     *
     * @return the boolean
     */
    boolean isSuccess();

    /**
     * get message type
     *
     * @return
     */
    @Override
    String toString();
}
