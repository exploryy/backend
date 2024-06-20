package com.hits.open.world.websocket.multipolygon;

import com.hits.open.world.core.location.UserLocationService;
import com.hits.open.world.core.multipolygon.MultipolygonService;
import com.hits.open.world.core.statistic.StatisticService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;

@SpringBootTest(classes = { MultipolygonService.class, UserLocationService.class, StatisticService.class, MultipolygonHandler.class })
public class MultipolygonHandlerTest {
	@Autowired
	private MultipolygonHandler multipolygonHandler;

	@MockBean
	private MultipolygonService multipolygonService;

	@MockBean
	private UserLocationService userLocationService;

	@MockBean
	private StatisticService statisticService;

	@Test
	public void afterConnectionClosed() {
		WebSocketSession session = null;
		CloseStatus closeStatus = null;
		multipolygonHandler.afterConnectionClosed(session, closeStatus);
	}
}
