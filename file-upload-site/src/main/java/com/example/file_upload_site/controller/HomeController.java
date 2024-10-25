package com.example.file_upload_site.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/") // 루트 URL 요청 처리
    public String home() {
        return "upload"; // 기본적으로 업로드 페이지로 이동
    }
}
