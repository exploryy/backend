package com.hits.open.world.core.buff;

import com.hits.open.world.core.buff.repository.entity.BuffEntity;
import com.hits.open.world.core.buff.repository.BuffRepository;
import com.hits.open.world.core.buff.repository.entity.ClientBufEntity;
import com.hits.open.world.core.buff.repository.ClientBufRepository;
import com.hits.open.world.core.buff.repository.enums.BuffStatus;
import com.hits.open.world.core.statistic.StatisticService;
import com.hits.open.world.public_interface.buff.BuffDto;
import com.hits.open.world.public_interface.exception.ExceptionInApplication;
import com.hits.open.world.public_interface.exception.ExceptionType;
import com.hits.open.world.util.LevelUtil;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Stream;

@Service
public class BuffService {
    private final BuffRepository buffRepository;
    private final ClientBufRepository clientBufRepository;
    private final StatisticService statisticService;

    public BuffService(BuffRepository buffRepository, ClientBufRepository clientBufRepository,
                       @Lazy StatisticService statisticService) {
        this.buffRepository = buffRepository;
        this.clientBufRepository = clientBufRepository;
        this.statisticService = statisticService;
    }

    @Transactional
    public List<BuffDto> getMyBuffs(String userId) {
        var buffs = clientBufRepository.findBuffsByUserId(userId);
        return buffs.stream()
                .map(this::mapToDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<BuffDto> getAll(Integer level) {
        if (level == null) {
            return buffRepository.findAll().stream()
                    .map(this::mapToDto)
                    .toList();
        }

        return buffRepository.findAllByLevel(level).stream()
                .map(this::mapToDto)
                .toList();
    }

    @Transactional
    public BuffDto applyBuff(Long buffId, String userId) {
        var user = statisticService.getUserStatistics(userId);
        var buff = buffRepository.findByBuffId(buffId)
                .orElseThrow(() -> new ExceptionInApplication("Buff not found", ExceptionType.NOT_FOUND));

        var level = LevelUtil.calculateLevel(user.experience());
        if (level != buff.levelNumber()) {
            throw new ExceptionInApplication("Level is not correct", ExceptionType.INVALID);
        }

        var clientBuff = clientBufRepository.findByBuffIdAndUserId(buffId, userId);
        if (clientBuff.isPresent()) {
            throw new ExceptionInApplication("Buff for the level already applied", ExceptionType.INVALID);
        }

        var clientBufEntity = ClientBufEntity.builder()
                .buffId(buffId)
                .clientId(userId)
                .build();
        clientBufRepository.save(clientBufEntity);
        return mapToDto(clientBufEntity);
    }

    @Transactional
    public BigDecimal getUserBuffs(String userId, BuffStatus buffStatus) {
        var userBuffs = getMyBuffs(userId);

        Stream<BuffDto> buffDtoStream = userBuffs.stream()
                .filter(buff -> buff.status().equals(buffStatus.name()));

        if (buffStatus.equals(BuffStatus.COINS)) {
            var currentBuffs = buffDtoStream.reduce(BigDecimal.ZERO, (acc, buff) -> acc.add(buff.valueFactor()), BigDecimal::add);
            return max(currentBuffs, BigDecimal.TWO);
        }

        return buffDtoStream.reduce(BigDecimal.ONE, (acc, buff) -> acc.multiply(buff.valueFactor()), BigDecimal::multiply);
    }

    private BuffDto mapToDto(BuffEntity entity) {
        return BuffDto.builder()
                .buffId(entity.buffId())
                .levelNumber(entity.levelNumber())
                .status(entity.status().name())
                .valueFactor(entity.valueFactor())
                .build();
    }

    private BuffDto mapToDto(ClientBufEntity entity) {
        var buff = buffRepository.findByBuffId(entity.buffId())
                .orElseThrow(() -> new RuntimeException("Buff not found"));

        return BuffDto.builder()
                .buffId(entity.buffId())
                .levelNumber(buff.levelNumber())
                .status(buff.status().name())
                .valueFactor(buff.valueFactor())
                .build();
    }

    private BigDecimal max(BigDecimal first, BigDecimal second) {
        return first.compareTo(second) >= 0 ? first : second;
    }

}
