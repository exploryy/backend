package com.hits.open.world.core.coin;

import com.hits.open.world.core.money.MoneyService;
import com.hits.open.world.public_interface.coin.CoinResponseDto;
import com.hits.open.world.public_interface.location.LocationDto;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
public class CoinServiceTest {
	@Autowired
	private CoinService coinService;

	@MockBean
	private MoneyService moneyService;

	@Test
	public void save() {

	}
}
