package com.harishkannarao.springboot.graphqlwebmvc;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.util.TestSocketUtils;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.DEFINED_PORT;

@SpringBootTest(webEnvironment = DEFINED_PORT)
@ActiveProfiles("it")
public abstract class AbstractBaseIntegrationTest {

    @DynamicPropertySource
    static void registerTestProperties(DynamicPropertyRegistry registry) {
        final int RANDOM_SERVER_PORT = TestSocketUtils.findAvailableTcpPort();
        registry.add("server.port", () -> String.valueOf(RANDOM_SERVER_PORT));
    }
}
