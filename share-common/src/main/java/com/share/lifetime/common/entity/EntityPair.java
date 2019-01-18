package com.share.lifetime.common.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class EntityPair<E> {

    private E firstEntity;
    private E secondEntity;
}
