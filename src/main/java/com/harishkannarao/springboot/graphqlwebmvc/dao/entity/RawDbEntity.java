package com.harishkannarao.springboot.graphqlwebmvc.dao.entity;

import java.time.Instant;

public record RawDbEntity(String data, Instant createdTime, Instant updatedTime) {
}
