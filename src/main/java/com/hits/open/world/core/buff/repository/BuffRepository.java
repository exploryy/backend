package com.hits.open.world.core.buff.repository;

import com.hits.open.world.core.buff.repository.entity.BuffEntity;

import java.util.List;
import java.util.Optional;

public interface BuffRepository {
    Optional<BuffEntity> findByBuffId(Long buffId);
    List<BuffEntity> findAll();
    List<BuffEntity> findAllByLevel(int levelNumber);
}
