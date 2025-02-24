package com.nft.member.param;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class OneClickLoginParam {
    @NotBlank
    private String mobile;

    private String inviteCode;
}
