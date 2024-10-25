package com.example.file_upload_site;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;

public class ExcelTemplateModifier {
	
	  public void modifyExcelTemplate(String filePath) {
	        try (FileInputStream inputStream = new FileInputStream(filePath);
	             Workbook workbook = new XSSFWorkbook(inputStream)) {

	            // 첫 번째 시트를 선택
	            Sheet sheet = workbook.getSheetAt(0);

	            // 셀 스타일 생성
	            CellStyle style = workbook.createCellStyle();
	            Font font = workbook.createFont();
	            font.setBold(true);
	            style.setFont(font);

	            // 헤더에 스타일 적용
	            Row headerRow = sheet.getRow(0);
	            for (Cell cell : headerRow) {
	                cell.setCellStyle(style);
	            }

	            // 예: 두 번째 열에 숫자 형식 적용
	            CellStyle numberStyle = workbook.createCellStyle();
	            numberStyle.setDataFormat(workbook.createDataFormat().getFormat("#,##0.00"));

	            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
	                Row row = sheet.getRow(i);
	                if (row != null) {
	                    Cell cell = row.getCell(1); // 두 번째 열
	                    if (cell != null) {
	                        cell.setCellStyle(numberStyle);
	                    }
	                }
	            }

	            // 파일 저장
	            try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
	                workbook.write(outputStream);
	            }

	            System.out.println("엑셀 양식이 성공적으로 수정되었습니다.");
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	}