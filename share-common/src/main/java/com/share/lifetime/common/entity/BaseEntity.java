package com.share.lifetime.common.entity;

import java.util.Date;

import com.google.common.base.MoreObjects;
import com.google.common.base.MoreObjects.ToStringHelper;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class BaseEntity {

    protected Long id;

    /**
     * 表达逻辑删除， 1 表示删除， 0 表示未删除。
     */
    protected Boolean isDeleted = false;

    protected String createdBy;

    protected Date gmtCreate;

    protected String lastModifiedBy;

    protected Date gmtModified;

    protected ToStringHelper toStringHelper() {
        return MoreObjects.toStringHelper(this).omitNullValues().add("id", id).add("createdBy", createdBy)
            .add("gmtCreate", gmtCreate).add("lastModifiedBy", lastModifiedBy).add("gmtModified", gmtModified);
    }

    public String toString() {
        return toStringHelper().toString();
    }

}
