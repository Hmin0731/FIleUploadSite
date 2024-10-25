package com.example.file_upload_site.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.file_upload_site.service.DeleteService;

import org.springframework.ui.Model;
import org.springframework.beans.factory.annotation.Autowired;

@Controller
public class DeleteController {

    @Autowired
    private DeleteService deleteService; // 서비스 클래스 주입

    @PostMapping("/report/delete")
    public String deleteData(@RequestParam("advertiserId") String advertiserId, Model model) {
        try {
            // 광고주 ID에 해당하는 데이터를 삭제합니다.
        	deleteService.deleteDataByAdvertiserId(advertiserId);
            model.addAttribute("message", "데이터가 성공적으로 삭제되었습니다.");
        } catch (Exception e) {
            model.addAttribute("message", "삭제 중 오류가 발생했습니다: " + e.getMessage());
        }
        return "upload"; // 이 부분은 HTML 페이지 이름으로 대체하세요.
    }
}
