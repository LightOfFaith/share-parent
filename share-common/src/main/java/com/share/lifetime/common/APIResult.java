package com.share.lifetime.common;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
//@JsonInclude(Include.NON_EMPTY)
public class APIResult<T> extends AbstractResult<T> {

    // @JsonProperty(value = "sub_code")
    protected String subCode;

    // @JsonProperty(value = "sub_msg")
    protected String subMsg;

    protected long timestamp;

}
