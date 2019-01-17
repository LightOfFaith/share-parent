package com.share.lifetime.common;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
//@JsonInclude(Include.NON_EMPTY)
public abstract class AbstractResult<T> {

    protected String code;

    protected String msg;

    protected T result;
}
