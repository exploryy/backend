package com.hits.open.world.core.money;

public interface MoneyRepository {
    void addMoney(String userId, int amount);

    void subtractMoney(String userId, int amount);

    int getMoney(String userId);

    void initializeMoney(String userId);
}
