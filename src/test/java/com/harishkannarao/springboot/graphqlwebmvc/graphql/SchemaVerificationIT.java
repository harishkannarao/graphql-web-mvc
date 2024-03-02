package com.harishkannarao.springboot.graphqlwebmvc.graphql;

import com.harishkannarao.springboot.graphqlwebmvc.AbstractBaseIT;
import com.harishkannarao.springboot.graphqlwebmvc.util.FileReaderUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

public class SchemaVerificationIT extends AbstractBaseIT {

    private final TestRestTemplate testRestTemplate;

    @Autowired
    public SchemaVerificationIT(TestRestTemplate testRestTemplate) {
        this.testRestTemplate = testRestTemplate;
    }

    @Test
    public void verify_resolved_schema() {
        String actualSchema = testRestTemplate.getForObject("/graphql/schema", String.class);

        String expectedSchema = FileReaderUtil.readFile("graphql-test/schema/expectedSchema.graphqls");
        assertThat(actualSchema).isEqualToNormalizingPunctuationAndWhitespace(expectedSchema);
    }
}
