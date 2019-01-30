package com.share.lifetime.common.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SignTypeEnum {

    MD5("MD5"), HMACSHA256("HMAC-SHA256");

    private String value;

}
