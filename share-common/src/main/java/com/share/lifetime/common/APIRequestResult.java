package com.share.lifetime.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonPropertyOrder(value = {"code", "msg", "sub_code", "sub_msg", "timestamp"})
public class APIRequestResult<T> extends AbstractRequestResult<T> {

    @JsonProperty(value = "sub_code")
    protected String subCode;

    @JsonProperty(value = "sub_msg")
    protected String subMsg;

    protected String url;

    protected long timestamp;

}
