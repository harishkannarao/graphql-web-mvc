package com.harishkannarao.springboot.graphqlwebmvc.model;

import java.time.Instant;

public record RawDbEntity(String data, Instant createdTime, Instant updatedTime) {
}
