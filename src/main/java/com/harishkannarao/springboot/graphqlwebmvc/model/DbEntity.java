package com.harishkannarao.springboot.graphqlwebmvc.model;

import java.time.Instant;

public record DbEntity(String data, Instant createdTime, Instant updatedTime) {
}
