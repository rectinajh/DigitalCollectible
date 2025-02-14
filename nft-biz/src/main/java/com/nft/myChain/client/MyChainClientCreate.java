package com.nft.myChain.client;

import com.alipay.mychain.sdk.api.MychainClient;
import com.alipay.mychain.sdk.api.env.ClientEnv;
import com.alipay.mychain.sdk.api.env.ISslOption;
import com.alipay.mychain.sdk.api.env.SignerOption;
import com.alipay.mychain.sdk.api.env.SslBytesOption;
import com.alipay.mychain.sdk.api.logging.AbstractLoggerFactory;
import com.alipay.mychain.sdk.api.logging.ILogger;
import com.alipay.mychain.sdk.crypto.MyCrypto;
import com.alipay.mychain.sdk.crypto.keyoperator.Pkcs8KeyOperator;
import com.alipay.mychain.sdk.crypto.keypair.Keypair;
import com.alipay.mychain.sdk.crypto.signer.SignerBase;
import com.alipay.mychain.sdk.errorcode.ErrorCode;
import com.alipay.mychain.sdk.utils.IOUtil;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

public class MyChainClientCreate {
    /**
     * sdk client
     */
    private static MychainClient sdk;

    /**
     * mychain environment
     */
    private static ClientEnv env;

    /**
     * client key password
     */
    private static String keyPassword = "Aa193802.";  //根据实际情况更新，申请证书时候指定的SSL密码

    /**
     * trustCa password.
     */
    private static String trustStorePassword = "mychain";


    private static Keypair userKeypair;

    /**
     * user password
     */
    private static String userPassword = "Aa199211."; // 根据实际情况更新，创建账户的密码

    /**
     * host ip
     */
    private static String host = "47.103.163.178";

    /**
     * server port
     */
    private static int port = 18130;


    public static void initMychainEnv() throws IOException {
        // any user key for sign message
        String userPrivateKeyFile = "user.key";
        Pkcs8KeyOperator pkcs8KeyOperator = new Pkcs8KeyOperator();
        userKeypair = pkcs8KeyOperator.load(IOUtil.inputStreamToByte(MyChainClientCreate.class.getClassLoader().getResourceAsStream(userPrivateKeyFile)), userPassword);

        env = buildMychainEnv();
        ILogger logger = AbstractLoggerFactory.getInstance(MyChainClientCreate.class);
        env.setLogger(logger);
    }
    public static ClientEnv buildMychainEnv() throws IOException {
        InetSocketAddress inetSocketAddress = InetSocketAddress.createUnresolved(host, port);
        String keyFilePath = "client.key";
        String certFilePath = "client.crt";
        String trustStoreFilePath = "trustCa";

        // build ssl option
        ISslOption sslOption = new SslBytesOption.Builder()
                .keyBytes(IOUtil.inputStreamToByte(MyChainClientCreate.class.getClassLoader().getResourceAsStream(keyFilePath)))
                .certBytes(IOUtil.inputStreamToByte(MyChainClientCreate.class.getClassLoader().getResourceAsStream(certFilePath)))
                .keyPassword(keyPassword)
                .trustStorePassword(trustStorePassword)
                .trustStoreBytes(
                        IOUtil.inputStreamToByte(MyChainClientCreate.class.getClassLoader().getResourceAsStream(trustStoreFilePath)))
                .build();

        List<InetSocketAddress> socketAddressArrayList = new ArrayList<InetSocketAddress>();
        socketAddressArrayList.add(inetSocketAddress);

        List<SignerBase> signerBaseList = new ArrayList<SignerBase>();
        SignerBase signerBase = MyCrypto.getInstance().createSigner(userKeypair);
        signerBaseList.add(signerBase);
        SignerOption signerOption = new SignerOption();
        signerOption.setSigners(signerBaseList);

        return ClientEnv.build(socketAddressArrayList, sslOption, signerOption);
    }
    public static MychainClient initSdk() throws IOException {
        initMychainEnv();
        sdk = new MychainClient();
        boolean initResult = sdk.init(env);
        if (!initResult) {
            exit("initSdk", "sdk init failed.");
        }
        return sdk;
    }
    public static void exit(String tag, String msg) {
        exit(String.format("%s error : %s ", tag, msg));
    }

    public static void exit(String msg) {
        System.out.println(msg);
        System.exit(0);
    }
    public static String getErrorMsg(int errorCode) {
        int minMychainSdkErrorCode = ErrorCode.SDK_INTERNAL_ERROR.getErrorCode();
        if (errorCode < minMychainSdkErrorCode) {
            return ErrorCode.valueOf(errorCode).getErrorDesc();
        } else {
            return ErrorCode.valueOf(errorCode).getErrorDesc();
        }
    }
}
