package com.example.file_upload_site.controller;

import com.example.file_upload_site.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.IOException;
import java.sql.SQLException;

@Controller
public class ExcelController {
	 private static final Logger logger = LoggerFactory.getLogger(ExcelController.class);
	 
    @Autowired
    private ReportService reportService;
    
   
    
    @PostMapping("/upload")
    public String uploadFiles(
    						   @RequestParam("advertiserId") String advertiserId,
                               @RequestParam("filesFolder1") MultipartFile filesFolder1, // Gmarket 상품별
                               @RequestParam("filesFolder2") MultipartFile filesFolder2, // Gmarket 일자별
                               @RequestParam("filesFolder3") MultipartFile filesFolder3, // Gmarket 키워드별
                               @RequestParam("filesFolder4") MultipartFile filesFolder4, // Auction 상품별
                               @RequestParam("filesFolder5") MultipartFile filesFolder5, // Auction 일자별
                               @RequestParam("filesFolder6") MultipartFile filesFolder6, // Auction 키워드별
                               Model model) {
        try {
            // Gmarket 보고서 삽입
            reportService.insertGmarketProductReport(filesFolder1, advertiserId);
            reportService.insertGmarketDatewiseReport(filesFolder2, advertiserId);
            reportService.insertGmarketKeywordReport(filesFolder3, advertiserId);

            // Auction 보고서 삽입
            reportService.insertAuctionProductReport(filesFolder4, advertiserId);
            reportService.insertAuctionDatewiseReport(filesFolder5, advertiserId);
            reportService.insertAuctionKeywordReport(filesFolder6, advertiserId);
            
            logger.info("파일 업로드 성공: " + filesFolder1.getOriginalFilename());
            logger.info("파일 업로드 성공: " + filesFolder2.getOriginalFilename());
            logger.info("파일 업로드 성공: " + filesFolder3.getOriginalFilename());
            logger.info("파일 업로드 성공: " + filesFolder4.getOriginalFilename());
            logger.info("파일 업로드 성공: " + filesFolder5.getOriginalFilename());
            logger.info("파일 업로드 성공: " + filesFolder6.getOriginalFilename());
            
            
            model.addAttribute("message", "파일 업로드 성공");
            
        } catch (IOException | SQLException e) {
        	logger.error("파일 업로드 실패: " + e.getMessage());
            model.addAttribute("message", "파일 업로드 실패: " + e.getMessage());
            
        }

        return "/upload"; // 업로드 페이지로 리다이렉트
    }
}
