package com.nft.member.param;

import lombok.Data;

import javax.validation.constraints.NotBlank;
@Data
public class SettingLoginPwdParam {
    @NotBlank
    private String mobile;
    @NotBlank
    private String pwd;

    private String inviteCode;
}
