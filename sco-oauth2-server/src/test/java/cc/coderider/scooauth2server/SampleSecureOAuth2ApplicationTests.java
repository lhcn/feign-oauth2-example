/*
 * Copyright 2012-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cc.coderider.scooauth2server;

import java.util.Base64;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.context.WebApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

/**
 * Series of automated integration tests to verify proper behavior of auto-configured,
 * OAuth2-secured system
 *
 * @author Greg Turnquist
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class SampleSecureOAuth2ApplicationTests {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private FilterChainProxy filterChain;

    private MockMvc mvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Before
    public void setUp() {
        this.mvc = webAppContextSetup(this.context).addFilters(this.filterChain).build();
        SecurityContextHolder.clearContext();
    }

    @Test
    public void everythingIsSecuredByDefault() throws Exception {
        this.mvc.perform(get("/").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized()).andDo(print());
    }

    @Test
    public void usePassword() throws Exception {
        String header = "Basic " + new String(Base64.getEncoder().encode("browser:browser-secret".getBytes()));
        MvcResult result = this.mvc
                .perform(post("/oauth/token").header("Authorization", header)
                        .param("grant_type", "password")
                        .param("scope", "ui")
                        .param("username", "user1")
                        .param("password", "password1"))
                .andExpect(status().isOk()).andDo(print()).andReturn();
        Object accessToken = this.objectMapper
                .readValue(result.getResponse().getContentAsString(), Map.class)
                .get("access_token");

        MvcResult getMe = this.mvc
                .perform(get("/me").accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(header().string("Content-Type",
                        MediaType.APPLICATION_JSON.toString() + ";charset=UTF-8"))
                .andExpect(status().isOk()).andDo(print()).andReturn();
    }

    @Test
    public void useClientCredentials() throws Exception {
        String header = "Basic " + new String(Base64.getEncoder().encode("micro-service:service-to-service-call-secret".getBytes()));
        MvcResult result = this.mvc
                .perform(post("/oauth/token").header("Authorization", header)
                        .param("grant_type", "client_credentials")
                        .param("scope", "service"))
                .andExpect(status().isOk()).andDo(print()).andReturn();
        Object accessToken = this.objectMapper
                .readValue(result.getResponse().getContentAsString(), Map.class)
                .get("access_token");

        MvcResult getMe = this.mvc
                .perform(get("/me").accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(header().string("Content-Type",
                        MediaType.APPLICATION_JSON.toString() + ";charset=UTF-8"))
                .andExpect(status().isOk()).andDo(print()).andReturn();
    }

}
