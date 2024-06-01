package com.hits.open.world.core.money;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import static com.example.open_the_world.public_.Tables.CLIENT_MONEY;

@Repository
@RequiredArgsConstructor
public class MoneyRepositoryImpl implements MoneyRepository {
    private final DSLContext create;

    @Override
    public void addMoney(String userId, int amount) {
        create.update(CLIENT_MONEY)
                .set(CLIENT_MONEY.MONEY, CLIENT_MONEY.MONEY.add(amount))
                .where(CLIENT_MONEY.CLIENT_ID.eq(userId))
                .execute();
    }

    @Override
    public void subtractMoney(String userId, int amount) {
        create.update(CLIENT_MONEY)
                .set(CLIENT_MONEY.MONEY, CLIENT_MONEY.MONEY.subtract(amount))
                .where(CLIENT_MONEY.CLIENT_ID.eq(userId))
                .execute();
    }

    @Override
    public int getMoney(String userId) {
        return create.select(CLIENT_MONEY.MONEY)
                .from(CLIENT_MONEY)
                .where(CLIENT_MONEY.CLIENT_ID.eq(userId))
                .fetchOne(CLIENT_MONEY.MONEY);
    }

    @Override
    public void initializeMoney(String userId) {
        create.insertInto(CLIENT_MONEY)
                .set(CLIENT_MONEY.CLIENT_ID, userId)
                .set(CLIENT_MONEY.MONEY, 0)
                .execute();
    }
}
