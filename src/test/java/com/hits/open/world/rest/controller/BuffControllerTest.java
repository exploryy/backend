package com.hits.open.world.rest.controller;

import com.hits.open.world.core.buff.BuffService;
import com.hits.open.world.rest.controller.buff.BuffController;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BuffController.class)
public class BuffControllerTest {
	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private BuffService buffService;

	@Test
	public void getAvailableBuffs() throws Exception {
		this.mockMvc.perform(get("/buff/available")
				.param("level", "123"))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
			.andExpect(jsonPath("$.<key>").value("<value>"));
	}
}
