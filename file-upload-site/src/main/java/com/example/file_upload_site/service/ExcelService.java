package com.example.file_upload_site.service;


import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Service
public class ExcelService {
    public void processExcel(MultipartFile file) throws IOException {
        try (InputStream is = file.getInputStream(); 
             Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheetAt(0);
            // TODO: 엑셀 데이터 처리 로직 추가

            // 결과를 새로운 엑셀 파일로 저장
            try (FileOutputStream fos = new FileOutputStream("processed_output.xlsx")) {
                workbook.write(fos);
            }
        }
    }
}
