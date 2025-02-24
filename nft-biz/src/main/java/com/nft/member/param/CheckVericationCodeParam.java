package com.nft.member.param;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @Author：liyun
 * @Package：com.nft.member.param
 * @Project：nftcode
 * @name：CheckVericationCodeParam
 * @Date：2025/2/24 10:22
 * @Filename：CheckVericationCodeParam
 */
@Data
public class CheckVericationCodeParam {
    @NotBlank
    private String mobile;
    @NotBlank
    private String verificationCode;
}
