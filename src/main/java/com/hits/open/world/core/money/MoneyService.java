package com.hits.open.world.core.money;

import com.hits.open.world.public_interface.exception.ExceptionInApplication;
import com.hits.open.world.public_interface.exception.ExceptionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MoneyService {
    private final MoneyRepository moneyRepository;

    public void addMoney(String userId, int amount) {
        moneyRepository.addMoney(userId, amount);
    }

    @Transactional
    public void subtractMoney(String userId, int amount) {
        var currentMoney = moneyRepository.getMoney(userId);
        if (currentMoney < amount) {
            throw new ExceptionInApplication("Not enough money", ExceptionType.INVALID);
        }
        moneyRepository.subtractMoney(userId, amount);
    }

    public int getUserMoney(String userId) {
        return moneyRepository.getMoney(userId);
    }
}
