package com.hits.open.world.core.money;

import com.hits.open.world.core.event.EventService;
import com.hits.open.world.core.event.EventType;
import com.hits.open.world.public_interface.event.EventDto;
import com.hits.open.world.public_interface.exception.ExceptionInApplication;
import com.hits.open.world.public_interface.exception.ExceptionType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MoneyService {
    private final MoneyRepository moneyRepository;
    private final EventService eventService;

    public void addMoney(String userId, int amount) {
        moneyRepository.addMoney(userId, amount);
        var currentMoney = moneyRepository.getMoney(userId);
        notifyUser(userId, currentMoney);
    }

    @Transactional
    public void subtractMoney(String userId, int amount) {
        var currentMoney = moneyRepository.getMoney(userId);
        if (currentMoney < amount) {
            throw new ExceptionInApplication("Not enough money", ExceptionType.INVALID);
        }
        moneyRepository.subtractMoney(userId, amount);
        notifyUser(userId, currentMoney - amount);
    }

    public int getUserMoney(String userId) {
        return moneyRepository.getMoney(userId);
    }

    private void notifyUser(String userId, int money) {
        try {
            eventService.sendEvent(userId, new EventDto("%d".formatted(money), EventType.CHANGE_MONEY));
        } catch (Exception e) {
            log.error("Failed to notify user about money update", e);
        }
    }
}
