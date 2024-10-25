package com.example.file_upload_site.controller;

import com.example.file_upload_site.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.sql.Connection;
import java.sql.DriverManager;

@Controller  // @RestController 대신 @Controller 사용
@RequestMapping("/report")
public class ReportController {

    private final ReportService reportService;

    @Autowired
    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/generate")
    public String generateReport(@RequestParam("advertiserId") String advertiserId) {
        String url = "jdbc:sqlserver://localhost;databaseName=UpLoadHm;encrypt=true;trustServerCertificate=true";
        String user = "sa";
        String password = "1234";

        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            reportService.generateExcelReport(connection, advertiserId);
            return "uploadReturn";  // templates 폴더에 있는 uploadReturn.html로 이동
        } catch (Exception e) {
            e.printStackTrace();
            return "error";  // 오류 시 error.html로 이동
        }
    }
}
