package com.harishkannarao.springboot.graphqlwebmvc.util;

import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.file.Files;

public final class FileReaderUtil {
    public static String readFile(String fileName) {
        try {
            return Files.readString(new ClassPathResource(fileName).getFile().toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
