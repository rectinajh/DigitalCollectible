package com.nft.myChain.service;

import com.alipay.mychain.sdk.api.MychainClient;
import com.alipay.mychain.sdk.api.env.ClientEnv;
import com.alipay.mychain.sdk.api.utils.Utils;
import com.alipay.mychain.sdk.crypto.hash.Hash;
import com.alipay.mychain.sdk.message.query.QueryTransactionResponse;
import com.alipay.mychain.sdk.message.transaction.account.DepositDataRequest;
import com.alipay.mychain.sdk.message.transaction.account.DepositDataResponse;
import com.nft.myChain.client.MyChainClientCreate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigInteger;

@Service
public class MyChainService {
    /**
     * baas上创建的帐户名字
     */
    private static final String account = "yunyun3"; // 根据实际情况更新，创建账户的名称

    /**
     * 存证上链
     *
     * @param data
     * @return
     */
    public String depositdata(String data) throws IOException {

            // init sdk client
            MychainClient sdk = MyChainClientCreate.initSdk();
            System.out.println("depositData start, data is: " + data);
            if (data.getBytes().length > 1024 * 1024) {
                MyChainClientCreate.exit("depositData", "data size more than 1Mb.");
            }

            // 构建存证交易
            DepositDataRequest request = new DepositDataRequest(Utils.getIdentityByName(account), Utils.getIdentityByName(account), data.getBytes(), BigInteger.ZERO);
            DepositDataResponse response = sdk.getAccountService().depositData(request);
            if (!response.isSuccess() || response.getTransactionReceipt().getResult() != 0) {
                MyChainClientCreate.exit("depositData", MyChainClientCreate.getErrorMsg((int) response.getTransactionReceipt().getResult()));
            } else {
                System.out.println("depositData end, deposit hash is: " + response.getTxHash());
                return response.getTxHash().hexStrValue();
            }
        return null;
    }
    /**
     * 链上查询
     *
     * @param txHash
     */
    public String queryTransaction(String txHash) throws IOException {
        String txData = "";
        //init sdk client
        MychainClient sdk = MyChainClientCreate.initSdk();
        if (txHash == null) {
            MyChainClientCreate.exit("queryTransaction", "txHash is empty.");
        }

        // 查询交易
        Hash hash = new Hash(txHash);
        QueryTransactionResponse queryTransactionResponse = sdk.getQueryService().queryTransaction(hash);
        if (!queryTransactionResponse.isSuccess()) {
            MyChainClientCreate.exit("queryTransaction", MyChainClientCreate.getErrorMsg(0));
        } else {
            txData = new String(queryTransactionResponse.getTransaction().getData());
            System.out.println("queryTransaction end, deposit data is: " + txData);
        }
        return txData;
    }
}
