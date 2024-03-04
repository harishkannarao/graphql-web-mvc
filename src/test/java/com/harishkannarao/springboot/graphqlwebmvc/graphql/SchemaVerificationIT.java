package com.harishkannarao.springboot.graphqlwebmvc.graphql;

import com.harishkannarao.springboot.graphqlwebmvc.AbstractBaseIT;
import com.harishkannarao.springboot.graphqlwebmvc.util.FileReaderUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

public class SchemaVerificationIT extends AbstractBaseIT {

    private final TestRestTemplate testRestTemplate;

    @Autowired
    public SchemaVerificationIT(TestRestTemplate testRestTemplate) {
        this.testRestTemplate = testRestTemplate;
    }

    @Test
    public void verify_resolved_schema() {
        ResponseEntity<String> schemaResponse = testRestTemplate.getForEntity("/graphql/schema", String.class);

        assertThat(schemaResponse.getStatusCode().value()).isEqualTo(200);
    }
}
