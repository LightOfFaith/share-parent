package com.share.lifetime.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.google.common.base.MoreObjects;
import com.google.common.base.MoreObjects.ToStringHelper;
import com.share.lifetime.common.util.JsonUtils;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(Include.NON_EMPTY)
public abstract class AbstractRequestResult<T> {

    protected String code;

    protected String msg;

    protected T result;

    protected ToStringHelper toStringHelper() {
        return MoreObjects.toStringHelper(this).omitNullValues().add("code", code).add("msg", msg).add("result",
            JsonUtils.toJSON(result));
    }

    public String toString() {
        return toStringHelper().toString();
    }

}
