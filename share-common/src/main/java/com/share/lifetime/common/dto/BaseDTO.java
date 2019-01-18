package com.share.lifetime.common.dto;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author liaoxiang
 * @date 2019/01/18
 */
@Getter
@Setter
public class BaseDTO {

    protected String dataChangeCreatedBy;

    protected String dataChangeLastModifiedBy;

    protected Date dataChangeCreatedTime;

    protected Date dataChangeLastModifiedTime;

}
