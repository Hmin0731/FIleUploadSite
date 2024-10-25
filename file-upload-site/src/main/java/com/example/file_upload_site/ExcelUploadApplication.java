package com.example.file_upload_site;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.example.file_upload_site")
public class ExcelUploadApplication {
    public static void main(String[] args) {
        SpringApplication.run(ExcelUploadApplication.class, args);
    }
}