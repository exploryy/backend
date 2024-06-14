package com.hits.open.world.core.coin;

import com.hits.open.world.core.coin.repository.CoinEntity;
import com.hits.open.world.core.coin.repository.CoinRepository;
import com.hits.open.world.public_interface.coin.CoinResponseDto;
import com.hits.open.world.public_interface.exception.ExceptionInApplication;
import com.hits.open.world.public_interface.exception.ExceptionType;
import com.hits.open.world.public_interface.location.LocationDto;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CoinService {
    private static final int COIN_CONST = 1;
    private final CoinRepository coinRepository;

    public CoinResponseDto save(LocationDto location) {
        //TODO generate value according to LEVEL
        var coinEntity = CoinEntity.builder()
                .taken(false)
                .value(COIN_CONST)
                .latitude(String.valueOf(location.latitude()))
                .longitude(String.valueOf(location.longitude()))
                .clientId(location.clientId())
                .build();

        var savedCoin = coinRepository.save(coinEntity);
        return mapCoin(savedCoin);
    }

    @Transactional
    public void deleteAllClientCoins(String clientId) {
        coinRepository.deleteAll(clientId);
    }

    @Transactional
    public List<CoinResponseDto> findAll(String clientId) {
        return coinRepository.findAll(clientId).stream()
                .map(this::mapCoin)
                .toList();
    }

    @Transactional
    public void consumeCoin(@NonNull Long id, String clientId) {
        var coin = coinRepository.findById(id).
                orElseThrow(() -> new ExceptionInApplication("Coin with this id not found", ExceptionType.NOT_FOUND));

        if (!coin.clientId().equals(clientId)) {
            throw new ExceptionInApplication("Client id mismatch", ExceptionType.NOT_FOUND);
        }

        //TODO Consume coin and update user balance, MoneyService should be used here

        coinRepository.deleteById(coin.coinId());
    }

    private CoinResponseDto mapCoin(CoinEntity coinEntity) {
        return new CoinResponseDto(
                coinEntity.coinId(),
                coinEntity.latitude(),
                coinEntity.longitude(),
                coinEntity.taken(),
                coinEntity.clientId(),
                coinEntity.value()
        );
    }

}
