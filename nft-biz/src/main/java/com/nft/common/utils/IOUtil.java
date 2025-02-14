package com.nft.common.utils;

import com.nft.business.ErrorCode;
import com.nft.business.NftBusinessException;
import org.apache.commons.io.FileUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class IOUtil {
    public static byte[] inputStreamToByte(InputStream inStream) throws IOException {
        int bufferSize = 1024;
        ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
        byte[] buff = new byte[bufferSize];
        int rc = 0;
        while ((rc = inStream.read(buff, 0, bufferSize)) > 0) {
            swapStream.write(buff, 0, rc);
        }
        return swapStream.toByteArray();
    }

    public static byte[] readFileToByteArray(String filePath) {
        try {
            return FileUtils.readFileToByteArray(new File(filePath));
        } catch (Exception e) {
            throw new NftBusinessException(ErrorCode.NFT_GET_FILE_INPUT_STREAM_FAILED,
                    "failed to convert file to bytes", e);
        }
    }
}
