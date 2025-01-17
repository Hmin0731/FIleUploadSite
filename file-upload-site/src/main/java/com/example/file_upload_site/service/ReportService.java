package com.example.file_upload_site.service;




import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;

import javax.sql.DataSource;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.util.Units;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTLineSer;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTPlotArea;
import org.openxmlformats.schemas.drawingml.x2006.main.CTShapeProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFChart;
import org.apache.poi.xddf.usermodel.XDDFColor;
import org.apache.poi.xddf.usermodel.XDDFLineProperties;
import org.apache.poi.xddf.usermodel.XDDFShapeProperties;
import org.apache.poi.xddf.usermodel.XDDFSolidFillProperties;
import org.apache.poi.xddf.usermodel.chart.*;
import org.apache.poi.xssf.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xddf.usermodel.chart.XDDFChartLegend;
import java.util.Map;


@Service
public class ReportService {

    @Autowired
    private DataSource dataSource;

    
    

    
    private static final Logger logger = LoggerFactory.getLogger(ReportService.class);
    
    // Gmarket 보고서 삽입 메서드들
    public void insertGmarketProductReport(MultipartFile file, String advertiserId) throws IOException, SQLException {

        try (Connection connection = dataSource.getConnection(); // 데이터소스에서 커넥션 가져오기
                PreparedStatement statement = connection.prepareStatement(
                		"INSERT INTO GmarketProductReport (Site, AdProductNumber, RelatedProductNumber, Impressions, Clicks, ClickThroughRate, AvgImpressionRank, AvgClickCost, TotalCost, Purchases, PurchaseAmount, ConversionRate, ReturnOnAdSpend, AdvertiserId) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);")) {

               // 엑셀 파일에서 데이터를 읽기
        	  Workbook workbook = WorkbookFactory.create(file.getInputStream());
              Sheet sheet = workbook.getSheetAt(0); // 첫 번째 시트 선택
              List<Row> rows = new ArrayList<>(); // 행을 저장할 리스트

              for (int i = 1; i <= sheet.getLastRowNum(); i++) { // 첫 번째 행은 헤더라고 가정하고 1부터 시작
                  Row row = sheet.getRow(i);
                  if (row != null) {
                      rows.add(row); // 행 추가
                  }
              }

               // 각 행에 대해 데이터베이스에 삽입
              for (Row row : rows) {
            	  statement.setString(1, row.getCell(0).getStringCellValue()); 
                  statement.setString(2, row.getCell(1).getStringCellValue());
                  statement.setString(3, row.getCell(2).getStringCellValue());
                  statement.setDouble(4, row.getCell(3).getNumericCellValue());
                  statement.setDouble(5, row.getCell(4).getNumericCellValue());
                  statement.setDouble(6, row.getCell(5).getNumericCellValue());
                  statement.setDouble(7, row.getCell(6).getNumericCellValue());
                  statement.setDouble(8, row.getCell(7).getNumericCellValue());
                  statement.setDouble(9, row.getCell(8).getNumericCellValue());
                  statement.setDouble(10, row.getCell(9).getNumericCellValue());
                  statement.setDouble(11, row.getCell(10).getNumericCellValue());
                  statement.setDouble(12, row.getCell(11).getNumericCellValue());
                  statement.setDouble(13, row.getCell(12).getNumericCellValue());
                  statement.setString(14, advertiserId); // AdvertiserId 추가
                  
                  // SQL 실행
                  statement.executeUpdate();
              }
              	logger.info("Gmarket 상품별 보고서 삽입 성공");
        } 		catch (SQLException e) {
            	logger.error("Gmarket 상품별 보고서 삽입 실패: " + e.getMessage());
            	throw e; // 예외를 다시 던져 호출자에게 알림      
        
        }
        
    }


    public void insertGmarketDatewiseReport(MultipartFile file, String advertiserId) throws IOException, SQLException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                 "INSERT INTO GmarketDatewiseReport (ReportDate, Impressions, Clicks, ClickThroughRate, AvgImpressionRank, AvgClickCost, TotalCost, Purchases, PurchaseAmount, ConversionRate, ReturnOnAdSpend, AdvertiserId) " +
                 "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);")) {

            // 엑셀 파일에서 데이터를 읽기
            Workbook workbook = WorkbookFactory.create(file.getInputStream());
            Sheet sheet = workbook.getSheetAt(0); // 첫 번째 시트 선택
            List<Row> rows = new ArrayList<>();

            for (int i = 1; i <= sheet.getLastRowNum(); i++) { // 첫 번째 행은 헤더라고 가정하고 1부터 시작
                Row row = sheet.getRow(i);
                if (row != null) {
                    rows.add(row);
                }
            }

            // 각 행에 대해 데이터베이스에 삽입
            for (Row row : rows) {
                try {
                    // ReportDate를 java.sql.Date 형식으로 안전하게 변환
                    java.sql.Date sqlDate;
                    Cell dateCell = row.getCell(0);

                    if (dateCell != null) {
                        if (dateCell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(dateCell)) {
                            // 날짜 형식인 경우
                            java.util.Date utilDate = dateCell.getDateCellValue();
                            sqlDate = new java.sql.Date(utilDate.getTime());
                        } else if (dateCell.getCellType() == CellType.STRING) {
                            // 문자열 형식인 경우, yyyy-MM-dd 포맷으로 변환
                            String dateStr = dateCell.getStringCellValue();
                            sqlDate = java.sql.Date.valueOf(dateStr);
                        } else {
                            throw new IllegalArgumentException("Invalid date format in cell: " + dateCell);
                        }

                        // 나머지 파라미터 설정
                        statement.setDate(1, sqlDate);
                        statement.setDouble(2, row.getCell(1).getNumericCellValue());
                        statement.setDouble(3, row.getCell(2).getNumericCellValue());
                        statement.setDouble(4, row.getCell(3).getNumericCellValue());
                        statement.setDouble(5, row.getCell(4).getNumericCellValue());
                        statement.setDouble(6, row.getCell(5).getNumericCellValue());
                        statement.setDouble(7, row.getCell(6).getNumericCellValue());
                        statement.setDouble(8, row.getCell(7).getNumericCellValue());
                        statement.setDouble(9, row.getCell(8).getNumericCellValue());
                        statement.setDouble(10, row.getCell(9).getNumericCellValue());
                        statement.setDouble(11, row.getCell(10).getNumericCellValue());
                        statement.setString(12, advertiserId);

                        // SQL 실행
                        statement.executeUpdate();
                    }
                } catch (Exception e) {
                    logger.error("Row insertion failed: " + e.getMessage(), e);
                }
            }
            logger.info("Gmarket 일자별 보고서 삽입 성공");
        } catch (SQLException e) {
            logger.error("Gmarket 일자별 보고서 삽입 실패: " + e.getMessage());
            throw e; // 예외를 다시 던져 호출자에게 알림
        }
    }


    public void insertGmarketKeywordReport(MultipartFile file, String advertiserId) throws IOException, SQLException {
//        insertGmarketReport(file, "GmarketKeywordReport"); // Gmarket 키워드별
        
       
        
        try (Connection connection = dataSource.getConnection(); // 데이터소스에서 커넥션 가져오기
                PreparedStatement statement = connection.prepareStatement(
                		"INSERT INTO GmarketKeywordReport (Keyword,Impressions,Clicks,ClickThroughRate,AvgImpressionRank,AvgClickCost,TotalCost,Purchases,PurchaseAmount,ConversionRate,ReturnOnAdSpend,AdvertiserId	) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? );")
                		){

               // 엑셀 파일에서 데이터를 읽기
               Workbook workbook = WorkbookFactory.create(file.getInputStream());
               Sheet sheet = workbook.getSheetAt(0); // 첫 번째 시트 선택
               List<Row> rows = new ArrayList<>(); // 행을 저장할 리스트

               for (int i = 1; i <= sheet.getLastRowNum(); i++) { // 첫 번째 행은 헤더라고 가정하고 1부터 시작
                   Row row = sheet.getRow(i);
                   if (row != null) {
                       rows.add(row); // 행 추가
                   }
               }

               // 각 행에 대해 데이터베이스에 삽입
               for (Row row : rows) {
                   // PreparedStatement에 파라미터 설정
               statement.setString(1, row.getCell(0).getStringCellValue()); 
               statement.setDouble(2, row.getCell(1).getNumericCellValue());
               statement.setDouble(3, row.getCell(2).getNumericCellValue());
               statement.setDouble(4, row.getCell(3).getNumericCellValue());
               statement.setDouble(5, row.getCell(4).getNumericCellValue());
               statement.setDouble(6, row.getCell(5).getNumericCellValue());
               statement.setDouble(7, row.getCell(6).getNumericCellValue());
               statement.setDouble(8, row.getCell(7).getNumericCellValue());
               statement.setDouble(9, row.getCell(8).getNumericCellValue());
               statement.setDouble(10, row.getCell(9).getNumericCellValue());
               statement.setDouble(11, row.getCell(10).getNumericCellValue());
               statement.setString(12, advertiserId); // AdvertiserId 추가
               // SQL 실행
               statement.executeUpdate();
               }
               	logger.info("Gmarket 키워드별 보고서 삽입 성공");
        	} 	catch (SQLException e) {
        		logger.error("Gmarket 키워드별 보고서 삽입 실패: " + e.getMessage());
        		throw e; // 예외를 다시 던져 호출자에게 알림
           }
    }

    // Auction 보고서 삽입 메서드들
    public void insertAuctionProductReport(MultipartFile file, String advertiserId) throws IOException, SQLException {
//        insertAuctionReport(file, "AuctionProductReport"); // Auction 상품별
        
     
        
        
        try (Connection connection = dataSource.getConnection(); // 데이터소스에서 커넥션 가져오기
                PreparedStatement statement = connection.prepareStatement(
                		"INSERT INTO AuctionProductReport (Site, AdProductNumber, RelatedProductNumber, Impressions, Clicks, ClickThroughRate, AvgImpressionRank, AvgClickCost, TotalCost, Purchases, PurchaseAmount, ConversionRate, ReturnOnAdSpend, AdvertiserId) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);")) {

               // 엑셀 파일에서 데이터를 읽기
               Workbook workbook = WorkbookFactory.create(file.getInputStream());
               Sheet sheet = workbook.getSheetAt(0); // 첫 번째 시트 선택
               List<Row> rows = new ArrayList<>(); // 행을 저장할 리스트

               for (int i = 1; i <= sheet.getLastRowNum(); i++) { // 첫 번째 행은 헤더라고 가정하고 1부터 시작
                   Row row = sheet.getRow(i);
                   if (row != null) {
                       rows.add(row); // 행 추가
                   }
               }

               // 각 행에 대해 데이터베이스에 삽입
               for (Row row : rows) {
                   // PreparedStatement에 파라미터 설정
               statement.setString(1, row.getCell(0).getStringCellValue()); 
               statement.setString(2, row.getCell(1).getStringCellValue());
               statement.setString(3, row.getCell(2).getStringCellValue());
               statement.setDouble(4, row.getCell(3).getNumericCellValue());
               statement.setDouble(5, row.getCell(4).getNumericCellValue());
               statement.setDouble(6, row.getCell(5).getNumericCellValue());
               statement.setDouble(7, row.getCell(6).getNumericCellValue());
               statement.setDouble(8, row.getCell(7).getNumericCellValue());
               statement.setDouble(9, row.getCell(8).getNumericCellValue());
               statement.setDouble(10, row.getCell(9).getNumericCellValue());
               statement.setDouble(11, row.getCell(10).getNumericCellValue());
               statement.setDouble(12, row.getCell(11).getNumericCellValue());
               statement.setDouble(13, row.getCell(12).getNumericCellValue());
               statement.setString(14, advertiserId); // AdvertiserId 추가
               // SQL 실행
               statement.executeUpdate();
               }
               	logger.info("Auction 상품별 보고서 삽입 성공");
        	} 	catch (SQLException e) {
        		logger.error("Auction 상품별 보고서 삽입 실패: " + e.getMessage());
        		throw e; // 예외를 다시 던져 호출자에게 알림
               
           }
    }

    public void insertAuctionDatewiseReport(MultipartFile file, String advertiserId) throws IOException, SQLException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                 "INSERT INTO AuctionDatewiseReport (ReportDate, Impressions, Clicks, ClickThroughRate, AvgImpressionRank, AvgClickCost, TotalCost, Purchases, PurchaseAmount, ConversionRate, ReturnOnAdSpend, AdvertiserId) " +
                 "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);")) {

            // 엑셀 파일에서 데이터를 읽기
            Workbook workbook = WorkbookFactory.create(file.getInputStream());
            Sheet sheet = workbook.getSheetAt(0); // 첫 번째 시트 선택
            List<Row> rows = new ArrayList<>();

            for (int i = 1; i <= sheet.getLastRowNum(); i++) { // 첫 번째 행은 헤더라고 가정하고 1부터 시작
                Row row = sheet.getRow(i);
                if (row != null) {
                    rows.add(row);
                }
            }

            // 각 행에 대해 데이터베이스에 삽입
            for (Row row : rows) {
                try {
                    // ReportDate를 java.sql.Date 형식으로 안전하게 변환
                    java.sql.Date sqlDate;
                    Cell dateCell = row.getCell(0);

                    if (dateCell != null) {
                        if (dateCell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(dateCell)) {
                            // 날짜 형식인 경우
                            java.util.Date utilDate = dateCell.getDateCellValue();
                            sqlDate = new java.sql.Date(utilDate.getTime());
                        } else if (dateCell.getCellType() == CellType.STRING) {
                            // 문자열 형식인 경우, yyyy-MM-dd 포맷으로 변환
                            String dateStr = dateCell.getStringCellValue();
                            sqlDate = java.sql.Date.valueOf(dateStr);
                        } else {
                            throw new IllegalArgumentException("Invalid date format in cell: " + dateCell);
                        }

                        // 나머지 파라미터 설정
                        statement.setDate(1, sqlDate);
                        statement.setDouble(2, row.getCell(1).getNumericCellValue());
                        statement.setDouble(3, row.getCell(2).getNumericCellValue());
                        statement.setDouble(4, row.getCell(3).getNumericCellValue());
                        statement.setDouble(5, row.getCell(4).getNumericCellValue());
                        statement.setDouble(6, row.getCell(5).getNumericCellValue());
                        statement.setDouble(7, row.getCell(6).getNumericCellValue());
                        statement.setDouble(8, row.getCell(7).getNumericCellValue());
                        statement.setDouble(9, row.getCell(8).getNumericCellValue());
                        statement.setDouble(10, row.getCell(9).getNumericCellValue());
                        statement.setDouble(11, row.getCell(10).getNumericCellValue());
                        statement.setString(12, advertiserId);

                        // SQL 실행
                        statement.executeUpdate();
                    }
                } catch (Exception e) {
                    logger.error("Row insertion failed: " + e.getMessage(), e);
                }
            }
            logger.info("Auction 일자별 보고서 삽입 성공");
        } catch (SQLException e) {
            logger.error("Auction 일자별 보고서 삽입 실패: " + e.getMessage());
            throw e; // 예외를 다시 던져 호출자에게 알림
        }
    }

    public void insertAuctionKeywordReport(MultipartFile file, String advertiserId) throws IOException, SQLException {
//        insertAuctionReport(file, "AuctionKeywordReport"); // Auction 키워드별
        
      
        try (Connection connection = dataSource.getConnection(); // 데이터소스에서 커넥션 가져오기
                PreparedStatement statement = connection.prepareStatement(
                		"INSERT INTO AuctionKeywordReport (Keyword,Impressions,Clicks,ClickThroughRate,AvgImpressionRank,AvgClickCost,TotalCost,Purchases,PurchaseAmount,ConversionRate,ReturnOnAdSpend,AdvertiserId	) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? );")
                		){

               // 엑셀 파일에서 데이터를 읽기
               Workbook workbook = WorkbookFactory.create(file.getInputStream());
               Sheet sheet = workbook.getSheetAt(0); // 첫 번째 시트 선택
               List<Row> rows = new ArrayList<>(); // 행을 저장할 리스트

               for (int i = 1; i <= sheet.getLastRowNum(); i++) { // 첫 번째 행은 헤더라고 가정하고 1부터 시작
                   Row row = sheet.getRow(i);
                   if (row != null) {
                       rows.add(row); // 행 추가
                   }
               }

               // 각 행에 대해 데이터베이스에 삽입
               for (Row row : rows) {
            	   statement.setString(1, row.getCell(0).getStringCellValue()); 
                   statement.setDouble(2, row.getCell(1).getNumericCellValue());
                   statement.setDouble(3, row.getCell(2).getNumericCellValue());
                   statement.setDouble(4, row.getCell(3).getNumericCellValue());
                   statement.setDouble(5, row.getCell(4).getNumericCellValue());
                   statement.setDouble(6, row.getCell(5).getNumericCellValue());
                   statement.setDouble(7, row.getCell(6).getNumericCellValue());
                   statement.setDouble(8, row.getCell(7).getNumericCellValue());
                   statement.setDouble(9, row.getCell(8).getNumericCellValue());
                   statement.setDouble(10, row.getCell(9).getNumericCellValue());
                   statement.setDouble(11, row.getCell(10).getNumericCellValue());
                   statement.setString(12, advertiserId); // AdvertiserId 추가
                   // SQL 실행
                   statement.executeUpdate();
                   }
                   	logger.info("Auction 키워드별 보고서 삽입 성공");
            	} 	catch (SQLException e) {
            		logger.error("Auction 키워드별 보고서 삽입 실패: " + e.getMessage());
            		throw e; // 예외를 다시 던져 호출자에게 알림
            	   
           }
        
    }

    	// DataSource를 주입받기 위한 생성자
    	public ReportService(DataSource dataSource) {
        this.dataSource = dataSource;
    	}
    	
    	

        public void generateExcelReport(Connection connection, String advertiserId) {
        	// 엑셀 워크북 생성
        	XSSFWorkbook workbook = new XSSFWorkbook();
        	
        	
        	
            XSSFSheet sheet1 = workbook.createSheet("월간 합산 Summary");

            XSSFSheet sheet4 = workbook.createSheet("월간_상품별_지마켓");
            XSSFSheet sheet5 = workbook.createSheet("월간_상품별_옥션");
            XSSFSheet sheet6 = workbook.createSheet("월간_키워드별_지마켓");
            XSSFSheet sheet7 = workbook.createSheet("월간_키워드별_옥션");
 
            
            
            // 그리드라인 비활성화
            sheet1.setDisplayGridlines(false);
            
            sheet4.setDisplayGridlines(false);
            sheet5.setDisplayGridlines(false);
            sheet6.setDisplayGridlines(false);
            sheet7.setDisplayGridlines(false);
            
            // 각 시트에 타이틀 추가
            addTitle01(sheet1, "월간 합산 Summary");
            
            addTitle02(sheet4, "월간_상품별_지마켓");
            addTitle02(sheet5, "월간_상품별_옥션");
            addTitle02(sheet6, "월간_키워드별_지마켓");
            addTitle02(sheet7, "월간_키워드별_옥션");

            
            XSSFFont font = workbook.createFont();
            font.setUnderline(XSSFFont.U_SINGLE);  // 밑줄 설정 (단일 밑줄)
            font.setBold(true);                 // 글자 굵게 설정
            // 셀 스타일 생성
            XSSFCellStyle style = workbook.createCellStyle();
            style.setFont(font);  // 폰트를 스타일에 적용
            XSSFRow row = sheet1.createRow(6); // 3번째 행 (0부터 시작하므로 2)
            XSSFCell cell = row.createCell(3); // 2번째 열 (0부터 시작하므로 1)
            cell.setCellValue("일자별 광고현황");
            
            XSSFRow row2 = sheet1.createRow(29); // 3번째 행 (0부터 시작하므로 2)
            XSSFCell cell2 = row2.createCell(3); // 2번째 열 (0부터 시작하므로 1)
            cell2.setCellValue("G마켓 일자별 광고현황");
            
            
            XSSFRow row3 = sheet1.createRow(54); // 3번째 행 (0부터 시작하므로 2)
            XSSFCell cell3 = row3.createCell(3); // 2번째 열 (0부터 시작하므로 1)
            cell3.setCellValue("옥션 일자별 광고현황");
            
            // 셀에 스타일 적용
            cell.setCellStyle(style);
            cell2.setCellStyle(style);
            cell3.setCellStyle(style);
            
        	// DataFormat 생성
        	DataFormat format = workbook.createDataFormat();  // DataFormat을 Workbook에서 가져와야 함

       	 	// 쉼표 형식 (예: 1,000,000)
        	XSSFCellStyle commaStyle = workbook.createCellStyle();
        	commaStyle.setDataFormat(format.getFormat("#,##0"));

        	// 퍼센트 형식 (예: 50.00%)
	        XSSFCellStyle percentStyle = workbook.createCellStyle();
	        percentStyle.setDataFormat(format.getFormat("0.00%"));

	     // D 열부터 N 열까지 열 너비를 10으로 설정
	        for (int i = 3; i <= 14; i++) { // D는 3, N은 13에 해당
	            sheet1.setColumnWidth(i, 10 * 256); // 10의 너비 설정
	        }

	        // C 열의 열 너비를 2로 설정
	        sheet1.setColumnWidth(2, 2 * 256); // C는 2에 해당
	        // O 열의 열 너비를 2로 설정
	        sheet1.setColumnWidth(15, 2 * 256); // N는 14에 해당
	        // C 열의 열 너비를 2로 설정
	        sheet1.setColumnWidth(1, 2 * 256); // C는 2에 해당
	        // O 열의 열 너비를 2로 설정
	        sheet1.setColumnWidth(16, 2 * 256); // N는 14에 해당
	     // 병합된 셀에 테두리 적용
            applyTopBorderToExistingCellsTop(workbook, sheet1, 1, 1, 2, 15);
            applyTopBorderToExistingCellsLeft(workbook, sheet1, 2, 98, 1, 1);
            applyTopBorderToExistingCellsRight(workbook, sheet1,2, 98, 16, 16);
            applyTopBorderToExistingCellsBottom(workbook, sheet1, 99, 99, 2, 15);
            applyTopBorderToExistingCellsTopLeft(workbook, sheet1, 1,1,1,1);
            applyTopBorderToExistingCellsTopRight(workbook, sheet1, 1, 1, 16, 16);
            applyTopBorderToExistingCellsLeftBottom(workbook, sheet1, 99, 99, 1, 1);
            applyTopBorderToExistingCellsRightBottom(workbook, sheet1, 99, 99, 16, 16);
            
            
            // C 열의 열 너비를 2로 설정
	        sheet4.setColumnWidth(1, 1 * 256); // B는 2에 해당
	        // O 열의 열 너비를 2로 설정
	        sheet4.setColumnWidth(14, 2 * 256); // O는 14에 해당

	     // C2:O2에 상단 테두리 적용
	        applyTopBorderToExistingCellsTop(workbook, sheet4, 1, 1, 2, 14);
	        //B3에서 B499까지의 범위:
	        applyTopBorderToExistingCellsLeft(workbook, sheet4, 2, 498, 1, 1);
	        //O3에서 O499까지의 범위:
            applyTopBorderToExistingCellsRight(workbook, sheet4, 2, 498, 14, 14);
	        // C500:O500에 하단 테두리 적용
	        applyTopBorderToExistingCellsBottom(workbook, sheet4, 499, 499, 2, 14);
	        // B2에 상단 좌측 테두리 적용
	        applyTopBorderToExistingCellsTopLeft(workbook, sheet4, 1, 1, 1, 1);
	        // P2에 상단 우측 테두리 적용
	        applyTopBorderToExistingCellsTopRight(workbook, sheet4, 1, 1, 14, 14);
	        // B500에 하단 좌측 테두리 적용
	        applyTopBorderToExistingCellsLeftBottom(workbook, sheet4, 499, 499, 1, 1);
	        // P500에 하단 우측 테두리 적용
	        applyTopBorderToExistingCellsRightBottom(workbook, sheet4, 499, 499, 14, 14);
	        
	        
//	=================================================================================================================================================
	     // C 열의 열 너비를 2로 설정
	        sheet5.setColumnWidth(1, 1 * 256); // B는 2에 해당
	        // O 열의 열 너비를 2로 설정
	        sheet5.setColumnWidth(14, 2 * 256); // O는 14에 해당
	        // C2:O2에 상단 테두리 적용
	        applyTopBorderToExistingCellsTop(workbook, sheet5, 1, 1, 2, 14);
	        //B3에서 B499까지의 범위:
	        applyTopBorderToExistingCellsLeft(workbook, sheet5, 2, 498, 1, 1);
	        //O3에서 O499까지의 범위:
            applyTopBorderToExistingCellsRight(workbook, sheet5, 2, 498, 14, 14);
	        // C500:O500에 하단 테두리 적용
	        applyTopBorderToExistingCellsBottom(workbook, sheet5, 499, 499, 2, 14);
	        // B2에 상단 좌측 테두리 적용
	        applyTopBorderToExistingCellsTopLeft(workbook, sheet5, 1, 1, 1, 1);
	        // P2에 상단 우측 테두리 적용
	        applyTopBorderToExistingCellsTopRight(workbook, sheet5, 1, 1, 14, 14);
	        // B500에 하단 좌측 테두리 적용
	        applyTopBorderToExistingCellsLeftBottom(workbook, sheet5, 499, 499, 1, 1);
	        // P500에 하단 우측 테두리 적용
	        applyTopBorderToExistingCellsRightBottom(workbook, sheet5, 499, 499, 14, 14);
	        
	        
//	    	=================================================================================================================================================
		     // C 열의 열 너비를 2로 설정
		        sheet6.setColumnWidth(1, 1 * 256); // c는 2에 해당
		        // O 열의 열 너비를 2로 설정
		        sheet6.setColumnWidth(13, 2 * 256); // O는 14에 해당
		        // C2:O2에 상단 테두리 적용
		        applyTopBorderToExistingCellsTop(workbook, sheet6, 1, 1, 2, 13);
		        //B3에서 B499까지의 범위:
		        applyTopBorderToExistingCellsLeft(workbook, sheet6, 2, 998, 1, 1);
		        //O3에서 O499까지의 범위:
	            applyTopBorderToExistingCellsRight(workbook, sheet6, 2, 998, 13, 13);
		        // C500:O500에 하단 테두리 적용
		        applyTopBorderToExistingCellsBottom(workbook, sheet6, 999, 999, 2, 13);
		        // B2에 상단 좌측 테두리 적용
		        applyTopBorderToExistingCellsTopLeft(workbook, sheet6, 1, 1, 1, 1);
		        // P2에 상단 우측 테두리 적용
		        applyTopBorderToExistingCellsTopRight(workbook, sheet6, 1, 1, 13, 13);
		        // B500에 하단 좌측 테두리 적용
		        applyTopBorderToExistingCellsLeftBottom(workbook, sheet6, 999, 999, 1, 1);
		        // P500에 하단 우측 테두리 적용
		        applyTopBorderToExistingCellsRightBottom(workbook, sheet6, 999, 999, 13, 13);
		        
//		    	=================================================================================================================================================
			     // C 열의 열 너비를 2로 설정
			        sheet7.setColumnWidth(1, 1 * 256); // B는 2에 해당
			        // O 열의 열 너비를 2로 설정
			        sheet7.setColumnWidth(13, 2 * 256); // O는 14에 해당
			        // C2:O2에 상단 테두리 적용
			        applyTopBorderToExistingCellsTop(workbook, sheet7, 1, 1, 2, 13);
			        //B3에서 B499까지의 범위:
			        applyTopBorderToExistingCellsLeft(workbook, sheet7, 2, 998, 1, 1);
			        //O3에서 O499까지의 범위:
		            applyTopBorderToExistingCellsRight(workbook, sheet7, 2, 998, 13, 13);
			        // C500:O500에 하단 테두리 적용
			        applyTopBorderToExistingCellsBottom(workbook, sheet7, 999, 999, 2, 13);
			        // B2에 상단 좌측 테두리 적용
			        applyTopBorderToExistingCellsTopLeft(workbook, sheet7, 1, 1, 1, 1);
			        // P2에 상단 우측 테두리 적용
			        applyTopBorderToExistingCellsTopRight(workbook, sheet7, 1, 1, 13, 13);
			        // B500에 하단 좌측 테두리 적용
			        applyTopBorderToExistingCellsLeftBottom(workbook, sheet7, 999, 999, 1, 1);
			        // P500에 하단 우측 테두리 적용
			        applyTopBorderToExistingCellsRightBottom(workbook, sheet7, 999, 999, 13, 13);
	        
	     // 예: B열부터 M열까지 너비를 12.38로 고정
	        setFixedColumnWidth(sheet1, 3, 12, 12.38); // B = 1, C = 2, ..., M = 12 (0-indexed 기준)
	        setFixedColumnWidth(sheet4, 2, 13, 12.38); // B = 1, C = 2, ..., M = 12 (0-indexed 기준)
	        setFixedColumnWidth(sheet5, 2, 13, 12.38); // B = 1, C = 2, ..., M = 12 (0-indexed 기준)
	        setFixedColumnWidth(sheet6, 2, 12, 12.38); // B = 1, C = 2, ..., M = 12 (0-indexed 기준)
	        setFixedColumnWidth(sheet7, 2, 12, 12.38); // B = 1, C = 2, ..., M = 12 (0-indexed 기준)
	        
         // 클릭수 차트 ==================================================================================
            XSSFDrawing drawing = sheet1.createDrawingPatriarch();
            XSSFClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0, 6, 8, 9, 20);

            // 차트 생성
            XSSFChart chart = drawing.createChart(anchor);
            chart.setTitleText("클릭수");
            chart.setTitleOverlay(false);

            // 축 설정 (카테고리 축 및 값 축)
            XDDFCategoryAxis bottomAxis = chart.createCategoryAxis(AxisPosition.BOTTOM);
            XDDFValueAxis leftAxis = chart.createValueAxis(AxisPosition.LEFT);
            leftAxis.setVisible(false); // 클릭수 차트의 Y축을 숨김

            // 데이터 소스 지정 (X축, Y축)
            XDDFDataSource<String> xAxis = XDDFDataSourcesFactory.fromStringCellRange(sheet1, new CellRangeAddress(22, 26, 3, 3)); // 날짜 구간
            XDDFNumericalDataSource<Double> yAxis = XDDFDataSourcesFactory.fromNumericCellRange(sheet1, new CellRangeAddress(22, 26, 5, 5)); // 클릭수 데이터 (F22:F26)

            // 차트 데이터
            XDDFBarChartData data = (XDDFBarChartData) chart.createData(ChartTypes.BAR, bottomAxis, leftAxis);
            data.setBarDirection(BarDirection.COL); // 세로 방향 설정
            XDDFBarChartData.Series series = (XDDFBarChartData.Series) data.addSeries(xAxis, yAxis);
            series.setTitle("클릭수", null);

         // 주황, 강조2 (RGB: 237, 125, 49)
            XDDFSolidFillProperties fillOrange = new XDDFSolidFillProperties(XDDFColor.from(new byte[]{(byte) 237, (byte) 125, (byte) 49}));
            XDDFShapeProperties shapePropertiesOrange = new XDDFShapeProperties();
            shapePropertiesOrange.setFillProperties(fillOrange);
         // 주황색을 적용하려면
            series.setShapeProperties(shapePropertiesOrange);
            // 플롯 데이터에 적용
            chart.plot(data);



	        // 총비용 차트 ==================================================================================           
	        XSSFDrawing drawing1 = sheet1.createDrawingPatriarch();
	        XSSFClientAnchor anchor1 = drawing1.createAnchor(0, 0, 0, 0, 9, 8, 12, 20);	

	        XSSFChart chart1 = drawing1.createChart(anchor1);
	        chart1.setTitleText("총비용");
	        chart1.setTitleOverlay(false);

	        XDDFCategoryAxis bottomAxis1 = chart1.createCategoryAxis(AxisPosition.BOTTOM);
	        XDDFValueAxis leftAxis1 = chart1.createValueAxis(AxisPosition.LEFT);
	        leftAxis1.setVisible(false); // 총비용 차트의 Y축을 숨김
	        XDDFDataSource<String> xAxis1 = XDDFDataSourcesFactory.fromStringCellRange(sheet1, new CellRangeAddress(22, 26, 3, 3)); // 날짜 구간
	     // 데이터 소스 지정 (총비용 데이터)
	        XDDFNumericalDataSource<Double> yAxis1 = XDDFDataSourcesFactory.fromNumericCellRange(sheet1, new CellRangeAddress(22, 26, 9, 9)); // 총비용 데이터 (J22:J26)


	        XDDFBarChartData data1 = (XDDFBarChartData) chart1.createData(ChartTypes.BAR, bottomAxis1, leftAxis1);
	        data1.setBarDirection(BarDirection.COL); // 세로 방향 설정
	        XDDFBarChartData.Series series1 = (XDDFBarChartData.Series) data1.addSeries(xAxis1, yAxis1);
	        series1.setTitle("총비용", null);
	     // 황금색, 강조4 (RGB: 255, 192, 0)
	        XDDFSolidFillProperties fillGold = new XDDFSolidFillProperties(XDDFColor.from(new byte[]{(byte) 255, (byte) 192, 0}));
	        XDDFShapeProperties shapePropertiesGold = new XDDFShapeProperties();
	        shapePropertiesGold.setFillProperties(fillGold);
	        

	     // 황금색을 적용하려면
	     series1.setShapeProperties(shapePropertiesGold);
	        chart1.plot(data1);

	        // 전환금액 차트 ==================================================================================
	        XSSFDrawing drawing2 = sheet1.createDrawingPatriarch();
	        XSSFClientAnchor anchor2 = drawing2.createAnchor(0, 0, 0, 0, 12, 8, 15, 20);	

	        XSSFChart chart2 = drawing2.createChart(anchor2);
	        chart2.setTitleText("광고수익률");
	        chart2.setTitleOverlay(false);

	        XDDFCategoryAxis bottomAxis2 = chart2.createCategoryAxis(AxisPosition.BOTTOM);
	        XDDFValueAxis leftAxis2 = chart2.createValueAxis(AxisPosition.LEFT);
	        leftAxis2.setVisible(false); // 광고수익률 차트의 Y축을 숨김
	        XDDFDataSource<String> xAxis2 = XDDFDataSourcesFactory.fromStringCellRange(sheet1, new CellRangeAddress(22, 26, 3, 3)); // 날짜 구간
	     // 데이터 소스 지정 (광고수익률 데이터)
	        XDDFNumericalDataSource<Double> yAxis2 = XDDFDataSourcesFactory.fromNumericCellRange(sheet1, new CellRangeAddress(22, 26, 12, 12)); // 광고수익률 데이터 (M22:M26)


	        XDDFBarChartData data2 = (XDDFBarChartData) chart2.createData(ChartTypes.BAR, bottomAxis2, leftAxis2);
	        data2.setBarDirection(BarDirection.COL); // 세로 방향 설정
	        XDDFBarChartData.Series series2 = (XDDFBarChartData.Series) data2.addSeries(xAxis2, yAxis2);
	        series2.setTitle("광고수익률", null);
	     // 녹색, 강조6 (RGB: 112, 173, 71)
	        XDDFSolidFillProperties fillGreen = new XDDFSolidFillProperties(XDDFColor.from(new byte[]{(byte) 112, (byte) 173, (byte) 71}));
	        XDDFShapeProperties shapePropertiesGreen = new XDDFShapeProperties();
	        shapePropertiesGreen.setFillProperties(fillGreen);

	     // 녹색을 적용하려면
	     series2.setShapeProperties(shapePropertiesGreen);
	        chart2.plot(data2);

	        // 노출수 차트 ==================================================================================
	        XSSFDrawing drawing3 = sheet1.createDrawingPatriarch();
	        XSSFClientAnchor anchor3 = drawing3.createAnchor(0, 0, 0, 0, 3, 8, 6, 20);

	        XSSFChart chart3 = drawing3.createChart(anchor3);
	        chart3.setTitleText("노출수");
	        chart3.setTitleOverlay(false);
	
	        
	        
	        XDDFCategoryAxis bottomAxis3 = chart3.createCategoryAxis(AxisPosition.BOTTOM);
	        XDDFValueAxis leftAxis3 = chart3.createValueAxis(AxisPosition.LEFT);
	        leftAxis3.setVisible(false); // 노출수 차트의 Y축을 숨김

	     // 데이터 소스 지정 (X축, Y축)
	        XDDFDataSource<String> xAxis3 = XDDFDataSourcesFactory.fromStringCellRange(sheet1, new CellRangeAddress(22, 26, 3, 3)); // 날짜 구간 (D22:D26)
	        XDDFNumericalDataSource<Double> yAxis3 = XDDFDataSourcesFactory.fromNumericCellRange(sheet1, new CellRangeAddress(22, 26, 4, 4)); // 노출수 데이터 (E22:E26)


	        XDDFBarChartData data3 = (XDDFBarChartData) chart3.createData(ChartTypes.BAR, bottomAxis3, leftAxis3);
	        data3.setBarDirection(BarDirection.COL); // 세로 방향 설정
	        XDDFBarChartData.Series series3 = (XDDFBarChartData.Series) data3.addSeries(xAxis3, yAxis3);
	        series3.setTitle("노출수", null);
	     // 파랑, 강조1 (RGB: 68, 114, 196)
	        XDDFSolidFillProperties fillBlue = new XDDFSolidFillProperties(XDDFColor.from(new byte[]{(byte) 68, (byte) 114, (byte) 196}));
	        XDDFShapeProperties shapePropertiesBlue = new XDDFShapeProperties();
	        shapePropertiesBlue.setFillProperties(fillBlue);
	     // 예를 들어 파란색을 적용하려면
	        series3.setShapeProperties(shapePropertiesBlue);
	        chart3.plot(data3);
	        
	        
	        
	     // 지마켓 점선 차트의 노출수 차트 ==================================================================================
	        XSSFDrawing drawing4 = sheet1.createDrawingPatriarch();
	        XSSFClientAnchor anchor4 = drawing4.createAnchor(0, 0, 0, 0, 3, 31, 6, 43);
	        // 3, 31: D32 위치를 의미 (D는 3번째 열이고, 32번째 행이므로 0-indexed 기준으로 31).
	        // 6, 43: F44 위치를 의미 (F는 6번째 열이고, 44번째 행이므로 0-indexed 기준으로 43).

	        XSSFChart chart4 = drawing4.createChart(anchor4);
	        chart4.setTitleText("노출수");
	        chart4.setTitleOverlay(false);

	        // Chart legend configuration
	        // XDDFChartLegend legend = chart4.getOrAddLegend();
	        // legend.setPosition(LegendPosition.TOP_RIGHT);

	        XDDFCategoryAxis bottomAxis4 = chart4.createCategoryAxis(AxisPosition.BOTTOM);
	        bottomAxis4.setTitle("");
	        XDDFValueAxis leftAxis4 = chart4.createValueAxis(AxisPosition.LEFT);
	        leftAxis4.setVisible(false);  // 왼쪽 축 숨기기

	        // 첫 번째 그래프 - 요일: D46:D52, 데이터: E46:E52
	        XDDFDataSource<String> xData = XDDFDataSourcesFactory.fromStringCellRange(sheet1, new CellRangeAddress(45, 51, 3, 3)); // 요일 D46:D52
	        XDDFNumericalDataSource<Double> yData = XDDFDataSourcesFactory.fromNumericCellRange(sheet1, new CellRangeAddress(45, 51, 4, 4)); // 데이터 E46:E52

	        XDDFLineChartData data4 = (XDDFLineChartData) chart4.createData(ChartTypes.LINE, bottomAxis4, leftAxis4);
	        XDDFLineChartData.Series series4 = (XDDFLineChartData.Series) data4.addSeries(xData, yData);
	        series4.setTitle("", null);
	        series4.setSmooth(false);
	        series4.setMarkerSize((short) 6);
	        series4.setMarkerStyle(MarkerStyle.CIRCLE);

	        // 파랑, 강조1 (RGB: 68, 114, 196)
	        XDDFSolidFillProperties fillBlue1 = new XDDFSolidFillProperties(XDDFColor.from(new byte[]{(byte) 68, (byte) 114, (byte) 196}));
	        XDDFLineProperties lineProperties = new XDDFLineProperties();
	        lineProperties.setFillProperties(fillBlue1);  // 선 색상 설정
	     // 선의 너비 설정 (0.5pt)
	        lineProperties.setWidth(2.5); // 여기에 Double 타입의 값을 사용합니다.

	        // 선 색상과 마커 색상을 설정하기 위해 lineProperties와 shapeProperties를 적용
	        XDDFShapeProperties shapePropertiesBlue1 = new XDDFShapeProperties();
	        shapePropertiesBlue1.setLineProperties(lineProperties);
	        series4.setShapeProperties(shapePropertiesBlue1);

	        chart4.plot(data4);


	        
	     // 지마켓 점선 차트의 전환수 차트 ==================================================================================
	        XSSFDrawing drawing5 = sheet1.createDrawingPatriarch();
	        XSSFClientAnchor anchor5 = drawing5.createAnchor(0, 0, 0, 0, 7, 31, 10, 43);

	        XSSFChart chart5 = drawing5.createChart(anchor5);
	        chart5.setTitleText("전환수");
	        chart5.setTitleOverlay(false);

	        XDDFCategoryAxis bottomAxis5 = chart5.createCategoryAxis(AxisPosition.BOTTOM);
	        bottomAxis5.setTitle("");
	        XDDFValueAxis leftAxis5 = chart5.createValueAxis(AxisPosition.LEFT);
	        leftAxis5.setVisible(false);  // 왼쪽 축 숨기기

	        XDDFDataSource<String> xData5 = XDDFDataSourcesFactory.fromStringCellRange(sheet1, new CellRangeAddress(45, 51, 3, 3)); // 요일 D46:D52
	        XDDFNumericalDataSource<Double> yData5 = XDDFDataSourcesFactory.fromNumericCellRange(sheet1, new CellRangeAddress(45, 51, 8, 8)); // 데이터 I46:I52

	        XDDFLineChartData data5 = (XDDFLineChartData) chart5.createData(ChartTypes.LINE, bottomAxis5, leftAxis5);
	        XDDFLineChartData.Series series5 = (XDDFLineChartData.Series) data5.addSeries(xData5, yData5);
	        series5.setTitle("", null);
	        series5.setSmooth(false);
	        series5.setMarkerSize((short) 6);
	        series5.setMarkerStyle(MarkerStyle.CIRCLE);

	        // 주황, 강조2 (RGB: 255, 165, 0)
	        XDDFSolidFillProperties fillOrange5 = new XDDFSolidFillProperties(XDDFColor.from(new byte[]{(byte) 255, (byte) 165, 0}));
	        XDDFLineProperties lineProperties5 = new XDDFLineProperties();
	        lineProperties5.setFillProperties(fillOrange5);
	        lineProperties5.setWidth(2.5);

	        XDDFShapeProperties shapePropertiesOrange5 = new XDDFShapeProperties();
	        shapePropertiesOrange5.setLineProperties(lineProperties5);
	        series5.setShapeProperties(shapePropertiesOrange5);

	        chart5.plot(data5);

	     // 지마켓 점선 차트의 광고수익률 차트 ==================================================================================
	        XSSFDrawing drawing6 = sheet1.createDrawingPatriarch();
	        XSSFClientAnchor anchor6 = drawing6.createAnchor(0, 0, 0, 0, 11, 31, 14, 43);

	        XSSFChart chart6 = drawing6.createChart(anchor6);
	        chart6.setTitleText("광고수익률");
	        chart6.setTitleOverlay(false);

	        XDDFCategoryAxis bottomAxis6 = chart6.createCategoryAxis(AxisPosition.BOTTOM);
	        bottomAxis6.setTitle("");
	        XDDFValueAxis leftAxis6 = chart6.createValueAxis(AxisPosition.LEFT);
	        leftAxis6.setVisible(false);  // 왼쪽 축 숨기기

	        XDDFDataSource<String> xData6 = XDDFDataSourcesFactory.fromStringCellRange(sheet1, new CellRangeAddress(45, 51, 3, 3)); // 요일 D46:D52
	        XDDFNumericalDataSource<Double> yData6 = XDDFDataSourcesFactory.fromNumericCellRange(sheet1, new CellRangeAddress(45, 51, 12, 12)); // 데이터 M46:M52

	        XDDFLineChartData data6 = (XDDFLineChartData) chart6.createData(ChartTypes.LINE, bottomAxis6, leftAxis6);
	        XDDFLineChartData.Series series6 = (XDDFLineChartData.Series) data6.addSeries(xData6, yData6);
	        series6.setTitle("", null);
	        series6.setSmooth(false);
	        series6.setMarkerSize((short) 6);
	        series6.setMarkerStyle(MarkerStyle.CIRCLE);

	        // 녹색, 강조6 (RGB: 0, 176, 80)
	        XDDFSolidFillProperties fillGreen6 = new XDDFSolidFillProperties(XDDFColor.from(new byte[]{0, (byte) 176, 80}));
	        XDDFLineProperties lineProperties6 = new XDDFLineProperties();
	        lineProperties6.setFillProperties(fillGreen6);
	        lineProperties6.setWidth(2.5);

	        XDDFShapeProperties shapePropertiesGreen6 = new XDDFShapeProperties();
	        shapePropertiesGreen6.setLineProperties(lineProperties6);
	        series6.setShapeProperties(shapePropertiesGreen6);

	        chart6.plot(data6);


	        
	     // 옥션 점선 차트의 노출수 차트 ==================================================================================
	        XSSFDrawing drawing7 = sheet1.createDrawingPatriarch();
	        XSSFClientAnchor anchor7 = drawing7.createAnchor(0, 0, 0, 0, 3, 56, 6, 68);

	        XSSFChart chart7 = drawing7.createChart(anchor7);
	        chart7.setTitleText("노출수");
	        chart7.setTitleOverlay(false);

	        XDDFCategoryAxis bottomAxis7 = chart7.createCategoryAxis(AxisPosition.BOTTOM);
	        bottomAxis7.setTitle("");
	        XDDFValueAxis leftAxis7 = chart7.createValueAxis(AxisPosition.LEFT);
	        leftAxis7.setVisible(false);

	        XDDFDataSource<String> xData7 = XDDFDataSourcesFactory.fromStringCellRange(sheet1, new CellRangeAddress(70, 76, 3, 3));
	        XDDFNumericalDataSource<Double> yData7 = XDDFDataSourcesFactory.fromNumericCellRange(sheet1, new CellRangeAddress(70, 76, 4, 4));

	        XDDFLineChartData data7 = (XDDFLineChartData) chart7.createData(ChartTypes.LINE, bottomAxis7, leftAxis7);
	        XDDFLineChartData.Series series7 = (XDDFLineChartData.Series) data7.addSeries(xData7, yData7);
	        series7.setTitle("", null);
	        series7.setSmooth(false);
	        series7.setMarkerSize((short) 6);
	        series7.setMarkerStyle(MarkerStyle.CIRCLE);

	        // 파랑, 강조1 (RGB: 68, 114, 196)
	        XDDFSolidFillProperties fillBlue7 = new XDDFSolidFillProperties(XDDFColor.from(new byte[]{(byte) 68, (byte) 114, (byte) 196}));
	        XDDFLineProperties lineProperties7 = new XDDFLineProperties();
	        lineProperties7.setFillProperties(fillBlue7);
	        lineProperties7.setWidth(2.5);

	        XDDFShapeProperties shapePropertiesBlue7 = new XDDFShapeProperties();
	        shapePropertiesBlue7.setLineProperties(lineProperties7);
	        series7.setShapeProperties(shapePropertiesBlue7);

	        chart7.plot(data7);

	        // 옥션 점선 차트의 전환수 차트 ==================================================================================
	        XSSFDrawing drawing8 = sheet1.createDrawingPatriarch();
	        XSSFClientAnchor anchor8 = drawing8.createAnchor(0, 0, 0, 0, 7, 56, 10, 68);

	        XSSFChart chart8 = drawing8.createChart(anchor8);
	        chart8.setTitleText("전환수");
	        chart8.setTitleOverlay(false);

	        XDDFCategoryAxis bottomAxis8 = chart8.createCategoryAxis(AxisPosition.BOTTOM);
	        bottomAxis8.setTitle("");
	        XDDFValueAxis leftAxis8 = chart8.createValueAxis(AxisPosition.LEFT);
	        leftAxis8.setVisible(false);

	        XDDFDataSource<String> xData8 = XDDFDataSourcesFactory.fromStringCellRange(sheet1, new CellRangeAddress(70, 76, 3, 3));
	        XDDFNumericalDataSource<Double> yData8 = XDDFDataSourcesFactory.fromNumericCellRange(sheet1, new CellRangeAddress(70, 76, 8, 8));

	        XDDFLineChartData data8 = (XDDFLineChartData) chart8.createData(ChartTypes.LINE, bottomAxis8, leftAxis8);
	        XDDFLineChartData.Series series8 = (XDDFLineChartData.Series) data8.addSeries(xData8, yData8);
	        series8.setTitle("", null);
	        series8.setSmooth(false);
	        series8.setMarkerSize((short) 6);
	        series8.setMarkerStyle(MarkerStyle.CIRCLE);

	        // 주황, 강조2 (RGB: 255, 165, 0)
	        XDDFSolidFillProperties fillOrange8 = new XDDFSolidFillProperties(XDDFColor.from(new byte[]{(byte) 255, (byte) 165, 0}));
	        XDDFLineProperties lineProperties8 = new XDDFLineProperties();
	        lineProperties8.setFillProperties(fillOrange8);
	        lineProperties8.setWidth(2.5);

	        XDDFShapeProperties shapePropertiesOrange8 = new XDDFShapeProperties();
	        shapePropertiesOrange8.setLineProperties(lineProperties8);
	        series8.setShapeProperties(shapePropertiesOrange8);

	        chart8.plot(data8);

	        // 옥션 점선 차트의 광고수익률 차트 ==================================================================================
	        XSSFDrawing drawing9 = sheet1.createDrawingPatriarch();
	        XSSFClientAnchor anchor9 = drawing9.createAnchor(0, 0, 0, 0, 11, 56, 14, 68);

	        XSSFChart chart9 = drawing9.createChart(anchor9);
	        chart9.setTitleText("광고수익률");
	        chart9.setTitleOverlay(false);

	        XDDFCategoryAxis bottomAxis9 = chart9.createCategoryAxis(AxisPosition.BOTTOM);
	        bottomAxis9.setTitle("");
	        XDDFValueAxis leftAxis9 = chart9.createValueAxis(AxisPosition.LEFT);
	        leftAxis9.setVisible(false);

	        XDDFDataSource<String> xData9 = XDDFDataSourcesFactory.fromStringCellRange(sheet1, new CellRangeAddress(70, 76, 3, 3));
	        XDDFNumericalDataSource<Double> yData9 = XDDFDataSourcesFactory.fromNumericCellRange(sheet1, new CellRangeAddress(70, 76, 12, 12));

	        XDDFLineChartData data9 = (XDDFLineChartData) chart9.createData(ChartTypes.LINE, bottomAxis9, leftAxis9);
	        XDDFLineChartData.Series series9 = (XDDFLineChartData.Series) data9.addSeries(xData9, yData9);
	        series9.setTitle("", null);
	        series9.setSmooth(false);
	        series9.setMarkerSize((short) 6);
	        series9.setMarkerStyle(MarkerStyle.CIRCLE);

	        // 녹색, 강조6 (RGB: 0, 176, 80)
	        XDDFSolidFillProperties fillGreen9 = new XDDFSolidFillProperties(XDDFColor.from(new byte[]{0, (byte) 176, 80}));
	        XDDFLineProperties lineProperties9 = new XDDFLineProperties();
	        lineProperties9.setFillProperties(fillGreen9);
	        lineProperties9.setWidth(2.5);

	        XDDFShapeProperties shapePropertiesGreen9 = new XDDFShapeProperties();
	        shapePropertiesGreen9.setLineProperties(lineProperties9);
	        series9.setShapeProperties(shapePropertiesGreen9);

	        chart9.plot(data9);

	        

//  ==================================================================================           
            
            
        
            
         
            try {
                // G마켓,옥션 일자별 통계
            	
            	String sql1 = "SELECT " +
            		    "CONCAT(SUBSTRING(CONVERT(VARCHAR(10), MIN(G.ReportDate), 23), 6, 5), '~', " +
            		    "SUBSTRING(CONVERT(VARCHAR(10), MAX(G.ReportDate), 23), 6, 5)) AS '구분', " +
            		    "SUM(G.Impressions + A.Impressions) AS '노출수', " +
            		    "SUM(G.Clicks + A.Clicks) AS '클릭수', " +
            		    "CAST(SUM(G.Clicks + A.Clicks) * 100.0 / NULLIF(SUM(G.Impressions + A.Impressions), 0) AS DECIMAL(5, 2)) AS '클릭률', " +
            		    "AVG(G.AvgClickCost + A.AvgClickCost) AS '평균클릭비용', " +
            		    "SUM(G.TotalCost + A.TotalCost) AS '총비용', " +
            		    "SUM(G.Purchases + A.Purchases) AS '전환수', " +
            		    "SUM(G.PurchaseAmount + A.PurchaseAmount) AS '전환금액', " +
            		    "CAST(SUM(G.ConversionRate + A.ConversionRate) * 100.0 / NULLIF(SUM(G.Clicks + A.Clicks), 0) AS DECIMAL(5, 2)) AS '전환율', " +
            		    "CAST(SUM(G.PurchaseAmount + A.PurchaseAmount) * 100.0 / NULLIF(SUM(G.TotalCost + A.TotalCost), 0) AS DECIMAL(5, 2)) AS '광고수익률' " +
            		    "FROM GmarketDatewiseReport G " +
            		    "JOIN AuctionDatewiseReport A ON G.ReportDate = A.ReportDate AND G.AdvertiserId = A.AdvertiserId " +
            		    "WHERE G.AdvertiserId LIKE ? " +
            		    "GROUP BY DATEDIFF(WEEK, DATEADD(DAY, 1 - DAY(G.ReportDate), G.ReportDate), G.ReportDate) " +
            		    "ORDER BY MIN(G.ReportDate);";
                
            	String sql2 = "SELECT " +
                        "LEFT(DATENAME(WEEKDAY, CONVERT(DATE, ReportDate, 120)), 1) AS 구분, " +
                        "SUM(Impressions) AS 노출수, " +
                        "SUM(Clicks) AS 클릭수, " +
                        "AVG(ClickThroughRate) AS 클릭률, " +
                        "AVG(AvgClickCost) AS 평균클릭비용, " +
                        "SUM(TotalCost) AS 총비용, " +
                        "SUM(Purchases) AS 전환수, " +
                        "SUM(PurchaseAmount) AS 전환금액, " +
                        "AVG(ConversionRate) AS 전환율, " +
                        "AVG(ReturnOnAdSpend) AS 광고수익률 " +
                        "FROM GmarketDatewiseReport " +
                        "WHERE AdvertiserId = ? " +
                        "GROUP BY LEFT(DATENAME(WEEKDAY, CONVERT(DATE, ReportDate, 120)), 1) " +
                        "ORDER BY CASE LEFT(DATENAME(WEEKDAY, CONVERT(DATE, ReportDate, 120)), 1) " +
                        "WHEN '월' THEN 1 " +
                        "WHEN '화' THEN 2 " +
                        "WHEN '수' THEN 3 " +
                        "WHEN '목' THEN 4 " +
                        "WHEN '금' THEN 5 " +
                        "WHEN '토' THEN 6 " +
                        "WHEN '일' THEN 7 " +
                        "END";
            	
            	String sql3 = "SELECT " +
                        "LEFT(DATENAME(WEEKDAY, CONVERT(DATE, ReportDate, 120)), 1) AS 구분, " +
                        "SUM(Impressions) AS 노출수, " +
                        "SUM(Clicks) AS 클릭수, " +
                        "AVG(ClickThroughRate) AS 클릭률, " +
                        "AVG(AvgClickCost) AS 평균클릭비용, " +
                        "SUM(TotalCost) AS 총비용, " +
                        "SUM(Purchases) AS 전환수, " +
                        "SUM(PurchaseAmount) AS 전환금액, " +
                        "AVG(ConversionRate) AS 전환율, " +
                        "AVG(ReturnOnAdSpend) AS 광고수익률 " +
                        "FROM AuctionDatewiseReport " +
                        "WHERE AdvertiserId = ? " +
                        "GROUP BY LEFT(DATENAME(WEEKDAY, CONVERT(DATE, ReportDate, 120)), 1) " +
                        "ORDER BY CASE LEFT(DATENAME(WEEKDAY, CONVERT(DATE, ReportDate, 120)), 1) " +
                        "WHEN '월' THEN 1 " +
                        "WHEN '화' THEN 2 " +
                        "WHEN '수' THEN 3 " +
                        "WHEN '목' THEN 4 " +
                        "WHEN '금' THEN 5 " +
                        "WHEN '토' THEN 6 " +
                        "WHEN '일' THEN 7 " +
                        "END";
            	addDataToSheet1(sheet1, connection, sql1, sql2, sql3, advertiserId, getCommonHeaders(), workbook);
                
                
                
                

                
                // G마켓 광고상품번호 통계
                String sqlGmarketProduct = "SELECT Site AS '사이트',AdProductNumber AS '광고상품번호',RelatedProductNumber AS '연관상품번호',Impressions AS '노출수',Clicks AS '클릭수',ClickThroughRate AS '클릭률',AvgImpressionRank AS '평균노출순위',AvgClickCost AS '평균클릭비용',TotalCost AS '총비용',Purchases AS '전환수',PurchaseAmount AS '전환금액',ConversionRate AS '전환율',ReturnOnAdSpend AS '광고수익률' FROM GmarketProductReport"
                						 + " WHERE AdvertiserId LIKE ? ;";
                		
                addDataToSheet4(sheet4, connection, sqlGmarketProduct, advertiserId, getGmarketProductHeaders(), workbook);

                // 옥션 광고상품번호 통계
                String sqlAuctionProduct = "SELECT Site AS '사이트',AdProductNumber AS '광고상품번호',RelatedProductNumber AS '연관상품번호',Impressions AS '노출수',Clicks AS '클릭수',ClickThroughRate AS '클릭률',AvgImpressionRank AS '평균노출순위',AvgClickCost AS '평균클릭비용',TotalCost AS '총비용',Purchases AS '전환수',PurchaseAmount AS '전환금액',ConversionRate AS '전환율',ReturnOnAdSpend AS '광고수익률' FROM AuctionProductReport"
						 				 + " WHERE AdvertiserId LIKE ? ;";
                addDataToSheet5(sheet5, connection, sqlAuctionProduct, advertiserId, getAuctionProductHeaders(), workbook);

                // G마켓 키워드 통계
                String sqlGmarketKeyword = "SELECT Keyword AS '키워드',Impressions AS '노출수',Clicks AS '클릭수',ClickThroughRate AS '클릭률',AvgImpressionRank AS '평균노출순위',AvgClickCost AS '평균클릭비용',TotalCost AS '총비용',Purchases AS '전환수',PurchaseAmount AS '전환금액',ConversionRate AS '전환율',ReturnOnAdSpend AS '광고수익률' FROM GmarketKeywordReport"
                						 + " WHERE AdvertiserId LIKE ? ;";
                addDataToSheet6(sheet6, connection, sqlGmarketKeyword, advertiserId, getGmarketKeywordHeaders(), workbook);

                // 옥션 키워드 통계
                String sqlAuctionKeyword = "SELECT Keyword AS '키워드',Impressions AS '노출수',Clicks AS '클릭수',ClickThroughRate AS '클릭률',AvgImpressionRank AS '평균노출순위',AvgClickCost AS '평균클릭비용',TotalCost AS '총비용',Purchases AS '전환수',PurchaseAmount AS '전환금액',ConversionRate AS '전환율',ReturnOnAdSpend AS '광고수익률' FROM AuctionKeywordReport"
						 				 + " WHERE AdvertiserId LIKE ? ;";
                addDataToSheet7(sheet7, connection, sqlAuctionKeyword, advertiserId, getAuctionKeywordHeaders(), workbook);

                
                
                
                
                
                

                try (FileOutputStream outputStream = new FileOutputStream("C:\\Users\\USER\\Desktop\\광고보고서.xlsx")) {
                    // 엑셀 파일 저장                	
                	workbook.write(outputStream);
                    
                }
                

            } 
            
            
            
            
            catch (Exception e) {
                e.printStackTrace();
            
            } 
            
            
            finally {
                try {
                    workbook.close();
                } catch (IOException e) {
                	
                	
                    e.printStackTrace();
                   
                    
                    
                }
            }
        }
//-----------------------------------------------------------------------------------------------------      
       


//-----------------------------------------------------------------------------------------------------        
                
        
        public void addTitle01(XSSFSheet sheetImpl, String titleText) {  // css. 추가
    	    // 시트의 첫 번째 행에 제목을 추가
    	    XSSFRow row = sheetImpl.createRow(2);  // 첫 번째 행
    	    XSSFCell cell = row.createCell(3);  // 첫 번째 셀
    	    cell.setCellValue(titleText);       // 셀에 제목 텍스트 설정
    	 // 셀 병합 
    	    sheetImpl.addMergedRegion(new CellRangeAddress(2, 2, 3, 14));
    	    
    	    // 셀 스타일 설정 (중앙 정렬, 굵게)
    	    XSSFCellStyle style = sheetImpl.getWorkbook().createCellStyle();
    	    XSSFFont font = sheetImpl.getWorkbook().createFont();
    	    font.setUnderline(XSSFFont.U_SINGLE);  // 밑줄 설정 (단일 밑줄)
    	    font.setBold(true);                 // 글자 굵게 설정
    	    style.setAlignment(HorizontalAlignment.CENTER); // 중앙 정렬
    	    style.setFont(font);
    	    cell.setCellStyle(style);           // 셀에 스타일 적용
    	    font.setFontHeightInPoints((short) 14);		// 제목 글꼴 크기 설정
    	    
    	  
    	    	
    	    
    	    
    	    
    	    
    
    	}
        
        public void addTitle02(XSSFSheet sheetImpl02, String titleText) {  // css. 추가
    	    // 시트의 첫 번째 행에 제목을 추가
    	    XSSFRow row = sheetImpl02.createRow(2);  // 첫 번째 행
    	    XSSFCell cell = row.createCell(2);  // 첫 번째 셀
    	    cell.setCellValue(titleText);       // 셀에 제목 텍스트 설정
    	    
    	    
    	    // 셀 스타일 설정 (중앙 정렬, 굵게)
    	    XSSFCellStyle style = sheetImpl02.getWorkbook().createCellStyle();
    	    XSSFFont font = sheetImpl02.getWorkbook().createFont();
    	    font.setBold(true);                 // 글자 굵게 설정
    	    font.setUnderline(XSSFFont.U_SINGLE);  // 밑줄 설정 (단일 밑줄)
//    	    style.setAlignment(HorizontalAlignment.CENTER); // 중앙 정렬
    	    style.setFont(font);
    	    cell.setCellStyle(style);           // 셀에 스타일 적용
    	    font.setFontHeightInPoints((short) 14);		// 제목 글꼴 크기 설정
    	    

    	    // 셀 병합 (A1:F1 병합)
//    	    sheetImpl02.addMergedRegion(new CellRangeAddress(1, 1, 1, 12));
    	    
    	    
    
    	    
    	}
        
    
    	
        


		// G마켓,옥션 일자별 통계			// 여기서 부터 해야함
        private String[] getCommonHeaders() {
            return new String[]{"구분", "노출수", "클릭수", "클릭률", "평균클릭비용",  "전환수", "총비용", "전환금액", "전환율", "광고수익률"};
        }

        
        // G마켓 광고상품번호 통계 헤더
        private String[] getGmarketProductHeaders() {
            return new String[]{"사이트","광고상품번호","노출수","클릭수","클릭률","평균클릭비용","평균노출순위","전환수","총비용","전환금액","전환율","광고수익률"};
        }

        // 옥션 광고상품번호 통계 헤더
        private String[] getAuctionProductHeaders() {
            return new String[]{"사이트","광고상품번호","노출수","클릭수","클릭률","평균클릭비용","평균노출순위","전환수","총비용","전환금액","전환율","광고수익률"};
        }

        // G마켓 키워드 통계 헤더
        private String[] getGmarketKeywordHeaders() {
            return new String[]{"키워드","노출수","클릭수","클릭률","평균클릭비용","평균노출순위","전환수","총비용","전환금액","전환율","광고수익률"};
        }

        // 옥션 키워드 통계 헤더
        private String[] getAuctionKeywordHeaders() {
            return new String[]{"키워드","노출수","클릭수","클릭률","평균클릭비용","평균노출순위","전환수","총비용","전환금액","전환율","광고수익률"};
        }

        // 시트에 데이터 추가_1번
        private void addDataToSheet1(XSSFSheet sheet1, Connection connection, String sql1, String sql2, String sql3, String advertiserId, String[] headers, XSSFWorkbook workbook) throws SQLException {
            try (PreparedStatement statement = connection.prepareStatement(sql1)) {			
                statement.setString(1, advertiserId);
                ResultSet resultSet = statement.executeQuery();
                int startRow1 = 22; // 첫 번째 데이터의 시작 위치
                
                addHeaderToSheet1(workbook , sheet1, headers, startRow1 - 1);
                populateSheetWithData1(sheet1, resultSet, workbook, startRow1);
                applyTopBorderToExistingCellsTopCenterBottom(workbook, sheet1, 21, 26, 3, 12);
//                autoSizeColumns(sheet1, headers.length);
            }
            
         // 두 번째 쿼리: 일자별 클릭광고 현황
            try (PreparedStatement statement2 = connection.prepareStatement(sql2)) {
                statement2.setString(1, advertiserId);
                ResultSet resultSet2 = statement2.executeQuery();
                int startRow2 = 45; // 두 번째 데이터의 시작 위치

                addHeaderToSheet1(workbook ,sheet1, headers, startRow2 - 1);  // 2번 헤더 추가 (34행에)
                populateSheetWithData1(sheet1, resultSet2, workbook, startRow2);  // 2번 데이터 추가 (35행부터)
                applyTopBorderToExistingCellsTopCenterBottom(workbook, sheet1, 44, 51, 3, 12);
//                applyTopBorderToExistingCellsTopCenterBottom(workbook, sheet1, 46, 46, 3, 12);
                // 자동 열 너비 조정 및 테두리 설정
//                adjustColumnsAndBorders(workbook, sheet1, startRow2, 11);
//                autoSizeColumns(sheet1, headers.length);
            }
            
         // 세 번째 쿼리: 포커스클릭 최근 광고 현황
            try (PreparedStatement statement3 = connection.prepareStatement(sql3)) {
                statement3.setString(1, advertiserId);
                ResultSet resultSet3 = statement3.executeQuery();
                int startRow3 = 70; // 세 번째 데이터의 시작 위치

                addHeaderToSheet1(workbook ,sheet1, headers, startRow3 - 1);  // 3번 헤더 추가 (48행에)
                populateSheetWithData1(sheet1, resultSet3, workbook, startRow3);  // 3번 데이터 추가 (49행부터)
                applyTopBorderToExistingCellsTopCenterBottom(workbook, sheet1, 69, 76, 3, 12);
//                autoSizeColumns(sheet1, headers.length);
                
                
                // 자동 열 너비 조정 및 테두리 설정
//                adjustColumnsAndBorders(workbook, sheet1, startRow3, 12);
            }
            // 열 너비를 텍스트 길이에 따라 자동 조정합니다.
           
            
        }
        
       
        
     // 시트에 데이터 추가_4번
        private void addDataToSheet4(XSSFSheet sheet4, Connection connection, String sql, String advertiserId, String[] headers, XSSFWorkbook workbook) throws SQLException {
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, advertiserId);
                ResultSet resultSet = statement.executeQuery();
                int startRow4 = 4; // 세 번째 데이터의 시작 위치

                addHeaderToSheet4(workbook, sheet4, headers, startRow4 - 1);
                populateSheetWithData4(sheet4, resultSet, workbook, startRow4);
                applyTopBorderToExistingCellsTopCenterBottom(workbook, sheet4, 3, 498, 2, 13);
//                autoSizeColumns(sheet4, headers.length);
                // 필터를 추가할 범위를 지정합니다.
                // 예를 들어, C4에서 N4까지 헤더 범위에 필터를 추가하려면:
                sheet4.setAutoFilter(new CellRangeAddress(startRow4 - 1, startRow4 - 1, 2, 13));
            }
            
        }

        
        // 시트에 데이터 추가_5번
        private void addDataToSheet5(XSSFSheet sheet5, Connection connection, String sql, String advertiserId, String[] headers , XSSFWorkbook workbook ) throws SQLException {
            try (PreparedStatement statement = connection.prepareStatement(sql)) {			
                statement.setString(1, advertiserId);
                ResultSet resultSet = statement.executeQuery();
                int startRow5 = 4; // 세 번째 데이터의 시작 위치
                addHeaderToSheet5(workbook, sheet5, headers, startRow5 - 1);
                populateSheetWithData5(sheet5, resultSet, workbook, startRow5);
                applyTopBorderToExistingCellsTopCenterBottom(workbook, sheet5, 3, 498, 2, 13);
                // 필터를 추가할 범위를 지정합니다.
                // 예를 들어, C4에서 N4까지 헤더 범위에 필터를 추가하려면:
                sheet5.setAutoFilter(new CellRangeAddress(startRow5 - 1, startRow5 - 1, 2, 13));
//                autoSizeColumns(sheet5, headers.length);
            }
//            autoSizeColumns(sheet5, headers.length);
        }
        
        // 시트에 데이터 추가_6번
        private void addDataToSheet6(XSSFSheet sheet6, Connection connection, String sql, String advertiserId, String[] headers, XSSFWorkbook workbook ) throws SQLException {
            try (PreparedStatement statement = connection.prepareStatement(sql)) {			
                statement.setString(1, advertiserId);
                ResultSet resultSet = statement.executeQuery();
                int startRow6 = 4; // 세 번째 데이터의 시작 위치
                addHeaderToSheet6(workbook, sheet6, headers, startRow6 - 1);
                populateSheetWithData6(sheet6, resultSet , workbook, startRow6);
                applyTopBorderToExistingCellsTopCenterBottom(workbook, sheet6, 3, 998, 2, 12);
                // 예를 들어, C4에서 N4까지 헤더 범위에 필터를 추가하려면:
                sheet6.setAutoFilter(new CellRangeAddress(startRow6 - 1, startRow6 - 1, 2, 12));
//                autoSizeColumns(sheet6, headers.length);
            }
//            autoSizeColumns(sheet6, headers.length);
        }
        
        // 시트에 데이터 추가_7번
        private void addDataToSheet7(XSSFSheet sheet7, Connection connection, String sql, String advertiserId, String[] headers, XSSFWorkbook workbook ) throws SQLException {
            try (PreparedStatement statement = connection.prepareStatement(sql)) {			
                statement.setString(1, advertiserId);
                ResultSet resultSet = statement.executeQuery();
                int startRow7 = 4; // 세 번째 데이터의 시작 위치
                addHeaderToSheet7(workbook, sheet7, headers, startRow7 - 1);
                populateSheetWithData7(sheet7, resultSet, workbook, startRow7 );
                applyTopBorderToExistingCellsTopCenterBottom(workbook, sheet7, 3, 998, 2, 12);
                // 예를 들어, C4에서 N4까지 헤더 범위에 필터를 추가하려면:
                sheet7.setAutoFilter(new CellRangeAddress(startRow7 - 1, startRow7 - 1, 2, 12));
//                autoSizeColumns(sheet7, headers.length);
            }
//            autoSizeColumns(sheet7, headers.length);
        }
        
        

        // 시트에 헤더 추가_1번
        private void addHeaderToSheet1(XSSFWorkbook workbook,  XSSFSheet sheet1, String[] headers, int headerRowNum) {
        	
            XSSFRow headerRow = sheet1.createRow(headerRowNum);
            
            for (int i = 0; i < headers.length; i++) {
                
            	Cell cell = headerRow.createCell(i + 3);	// + 1 으로 제일 왼쪽 1열 건너뛰기
                
                cell.setCellValue(headers[i]);
                
                XSSFCellStyle headerStyle = sheet1.getWorkbook().createCellStyle();
                XSSFColor orangeColor = new XSSFColor(new byte[] {(byte) 253, (byte) 233, (byte) 217}, null);
                headerStyle.setFillForegroundColor(orangeColor);
                headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                headerStyle.setAlignment(HorizontalAlignment.CENTER); // 중앙 정렬
                
                XSSFFont headerFont = sheet1.getWorkbook().createFont();
                headerFont.setBold(true);
                
                headerStyle.setFont(headerFont);
                cell.setCellStyle(headerStyle);
                
                
//                
            }
//            applyTopBorderToExistingCellsTopCenterBottom(workbook, sheet1, 21, 21, 3, 12);
            
            
            
        }
     // 시트에 헤더 추가_4번
        private void addHeaderToSheet4(XSSFWorkbook workbook,  XSSFSheet sheet4, String[] headers, int headerRowNum) {
        	XSSFRow headerRow = sheet4.createRow(headerRowNum);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i + 2);
                cell.setCellValue(headers[i]);
                
                XSSFCellStyle headerStyle = sheet4.getWorkbook().createCellStyle();
                XSSFColor orangeColor = new XSSFColor(new byte[] {(byte) 253, (byte) 233, (byte) 217}, null);
                headerStyle.setFillForegroundColor(orangeColor);
                headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                headerStyle.setAlignment(HorizontalAlignment.CENTER); // 중앙 정렬
                XSSFFont headerFont = sheet4.getWorkbook().createFont();
                headerFont.setBold(true);
                
                headerStyle.setFont(headerFont);
                cell.setCellStyle(headerStyle);
//                autoSizeColumns(sheet4, headers.length);
            }
        }
        
     // 시트에 헤더 추가_5번
        private void addHeaderToSheet5(XSSFWorkbook workbook,  XSSFSheet sheet5, String[] headers, int headerRowNum) {
        	XSSFRow headerRow = sheet5.createRow(headerRowNum);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i + 2);
                cell.setCellValue(headers[i]);
                
                XSSFCellStyle headerStyle = sheet5.getWorkbook().createCellStyle();
                XSSFColor orangeColor = new XSSFColor(new byte[] {(byte) 253, (byte) 233, (byte) 217}, null);
                headerStyle.setFillForegroundColor(orangeColor);
                headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                headerStyle.setAlignment(HorizontalAlignment.CENTER); // 중앙 정렬
                XSSFFont headerFont = sheet5.getWorkbook().createFont();
                headerFont.setBold(true);
                headerStyle.setFont(headerFont);
                cell.setCellStyle(headerStyle);
//                autoSizeColumns(sheet5, headers.length);
            }
        }
        
     // 시트에 헤더 추가_6번
        private void addHeaderToSheet6(XSSFWorkbook workbook,  XSSFSheet sheet6, String[] headers, int headerRowNum) {
        	XSSFRow headerRow = sheet6.createRow(headerRowNum);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i + 2);
                cell.setCellValue(headers[i]);
                
                XSSFCellStyle headerStyle = sheet6.getWorkbook().createCellStyle();
                XSSFColor orangeColor = new XSSFColor(new byte[] {(byte) 253, (byte) 233, (byte) 217}, null);
                headerStyle.setFillForegroundColor(orangeColor);
                headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                headerStyle.setAlignment(HorizontalAlignment.CENTER); // 중앙 정렬
                XSSFFont headerFont = sheet6.getWorkbook().createFont();
                headerFont.setBold(true);
                headerStyle.setFont(headerFont);
                cell.setCellStyle(headerStyle);
//                autoSizeColumns(sheet6, headers.length);
            }
        }
        
     // 시트에 헤더 추가_7번
        private void addHeaderToSheet7(XSSFWorkbook workbook,  XSSFSheet sheet7, String[] headers, int headerRowNum) {
        	XSSFRow headerRow = sheet7.createRow(headerRowNum);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i + 2);
                cell.setCellValue(headers[i]);
                
                XSSFCellStyle headerStyle = sheet7.getWorkbook().createCellStyle();
                XSSFColor orangeColor = new XSSFColor(new byte[] {(byte) 253, (byte) 233, (byte) 217}, null);
                headerStyle.setFillForegroundColor(orangeColor);
                headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                headerStyle.setAlignment(HorizontalAlignment.CENTER); // 중앙 정렬
                XSSFFont headerFont = sheet7.getWorkbook().createFont();
                headerFont.setBold(true);
                headerStyle.setFont(headerFont);
                cell.setCellStyle(headerStyle);
//                autoSizeColumns(sheet7, headers.length);
            }
        }
        
        

     // 시트에 데이터 추가_1번_일자별_G/A 통합
        private void populateSheetWithData1(XSSFSheet sheet1, ResultSet resultSet, XSSFWorkbook workbook, int startRows) throws SQLException {
            int rowNum = startRows; // 데이터 행 시작 위치

            // DataFormat 생성
            DataFormat format = workbook.createDataFormat();

            // 쉼표 형식 (예: 1,000)
            XSSFCellStyle commaStyle = workbook.createCellStyle();
            commaStyle.setDataFormat(format.getFormat("#,##0"));
            commaStyle.setAlignment(HorizontalAlignment.CENTER); // 가운데 정렬 추가

            // 쉼표와 퍼센트 형식 (예: 50.00%)
            XSSFCellStyle commaPercentStyle = workbook.createCellStyle();
            commaPercentStyle.setDataFormat(format.getFormat("#,##0.00%"));
            commaPercentStyle.setAlignment(HorizontalAlignment.CENTER); // 가운데 정렬 추가

            while (resultSet.next()) {
                XSSFRow dataRow = (XSSFRow) sheet1.createRow(rowNum++);

                dataRow.createCell(3).setCellValue(resultSet.getString("구분"));

                XSSFCell cell2 = dataRow.createCell(4);
                cell2.setCellValue(resultSet.getInt("노출수"));
                cell2.setCellStyle(commaStyle);

                XSSFCell cell3 = dataRow.createCell(5);
                cell3.setCellValue(resultSet.getInt("클릭수"));
                cell3.setCellStyle(commaStyle);

                XSSFCell cell4 = dataRow.createCell(6);
                cell4.setCellValue(resultSet.getDouble("클릭률") / 100); // 값을 100으로 나눔
                cell4.setCellStyle(commaPercentStyle); // 퍼센트 형식 적용

                XSSFCell cell5 = dataRow.createCell(7);
                cell5.setCellValue(resultSet.getInt("평균클릭비용"));
                cell5.setCellStyle(commaStyle);

                XSSFCell cell7 = dataRow.createCell(8);
                cell7.setCellValue(resultSet.getInt("전환수"));
                cell7.setCellStyle(commaStyle);

                XSSFCell cell8 = dataRow.createCell(9);
                cell8.setCellValue(resultSet.getInt("총비용"));
                cell8.setCellStyle(commaStyle);

                XSSFCell cell9 = dataRow.createCell(10);
                cell9.setCellValue(resultSet.getInt("전환금액"));
                cell9.setCellStyle(commaStyle);

                XSSFCell cell10 = dataRow.createCell(11);
                cell10.setCellValue(resultSet.getDouble("전환율") / 100); // 값을 100으로 나눔
                cell10.setCellStyle(commaPercentStyle); // 퍼센트 형식 적용

                XSSFCell cell11 = dataRow.createCell(12);
                cell11.setCellValue(resultSet.getDouble("광고수익률") / 100); // 값을 100으로 나눔
                cell11.setCellStyle(commaPercentStyle); // 퍼센트 형식 적용
            }

            // 데이터를 모두 추가한 후에 테두리 적용
            applyTopBorderToExistingCellsLeft(workbook, sheet1, 2, 98, 1, 1);
            applyTopBorderToExistingCellsRight(workbook, sheet1, 2, 98, 16, 16);
//            autoSizeColumns(sheet1, 20);
        }




     
        // 시트에 데이터 추가_4번_상품별_G마켓
        private void populateSheetWithData4(XSSFSheet sheet4, ResultSet resultSet, XSSFWorkbook workbook, int startRows) throws SQLException {
            int rowNum = startRows; // 데이터 행 시작 위치
            
            
            // DataFormat 생성
            DataFormat format = workbook.createDataFormat();

            // 쉼표 형식 (예: 1,000)
            XSSFCellStyle commaStyle = workbook.createCellStyle();
            commaStyle.setDataFormat(format.getFormat("#,##0"));
            commaStyle.setAlignment(HorizontalAlignment.CENTER); // 가운데 정렬 추가
            // 쉼표와 퍼센트 형식 (예: 50.00%)
            XSSFCellStyle commaPercentStyle = workbook.createCellStyle();
            commaPercentStyle.setDataFormat(format.getFormat("#,##0.00%"));
            commaPercentStyle.setAlignment(HorizontalAlignment.CENTER); // 가운데 정렬 추가
            // 쉼표와 일반 소수점 형식 (예: 1,000.00)
            XSSFCellStyle commaDoubleStyle = workbook.createCellStyle();
            commaDoubleStyle.setDataFormat(format.getFormat("#,##0.00"));
            commaDoubleStyle.setAlignment(HorizontalAlignment.CENTER); // 가운데 정렬 추가
            while (resultSet.next()) {
            	
                XSSFRow dataRow = (XSSFRow) sheet4.createRow(rowNum++);
                dataRow.createCell(2).setCellValue(resultSet.getString("사이트").toString());
                dataRow.createCell(3).setCellValue(resultSet.getString("광고상품번호"));
                
               
             // 정수형에 쉼표 형식 적용
                XSSFCell cell3 = dataRow.createCell(4);
                cell3.setCellValue(resultSet.getInt("노출수"));
                cell3.setCellStyle(commaStyle);

                XSSFCell cell4 = dataRow.createCell(5);
                cell4.setCellValue(resultSet.getInt("클릭수"));
                cell4.setCellStyle(commaStyle);
               
                // 실수형에 쉼표와 퍼센트 형식 적용
                XSSFCell cell5 = dataRow.createCell(6);
                cell5.setCellValue(resultSet.getDouble("클릭률") / 100); // 값을 100으로 나눔
                cell5.setCellStyle(commaPercentStyle); // 퍼센트 형식 적용
                
                XSSFCell cell6 = dataRow.createCell(7);
                cell6.setCellValue(resultSet.getInt("평균클릭비용"));
                cell6.setCellStyle(commaStyle);

                XSSFCell cell7 = dataRow.createCell(8);
                cell7.setCellValue(resultSet.getDouble("평균노출순위"));
                cell7.setCellStyle(commaDoubleStyle); // 소수점 쉼표 형식 적용

                XSSFCell cell8 = dataRow.createCell(9);
                cell8.setCellValue(resultSet.getInt("전환수"));
                cell8.setCellStyle(commaStyle);

                XSSFCell cell9 = dataRow.createCell(10);
                cell9.setCellValue(resultSet.getInt("총비용"));
                cell9.setCellStyle(commaStyle);

                XSSFCell cell10 = dataRow.createCell(11);
                cell10.setCellValue(resultSet.getInt("전환금액"));
                cell10.setCellStyle(commaStyle);

                XSSFCell cell11 = dataRow.createCell(12);
                cell11.setCellValue(resultSet.getDouble("전환율") / 100); // 값을 100으로 나눔
                cell11.setCellStyle(commaPercentStyle); // 퍼센트 형식 적용

                XSSFCell cell12 = dataRow.createCell(13);
                cell12.setCellValue(resultSet.getDouble("광고수익률") / 100); // 값을 100으로 나눔
                cell12.setCellStyle(commaPercentStyle); // 퍼센트 형식 적용
            }
            // 데이터를 모두 추가한 후에 테두리 적용
   	        //B3에서 B499까지의 범위:
	        applyTopBorderToExistingCellsLeft(workbook, sheet4, 2, 498, 1, 1);
	        //O3에서 O499까지의 범위:
            applyTopBorderToExistingCellsRight(workbook, sheet4, 2, 498, 14, 14);
//            autoSizeColumns(sheet4, 20);
            
        }
     // 시트에 데이터 추가_5번_상품별_옥션
        private void populateSheetWithData5(XSSFSheet sheet5, ResultSet resultSet, XSSFWorkbook workbook, int startRows) throws SQLException {
            int rowNum = startRows ; // 데이터 행 시작 위치
            
            // DataFormat 생성
            DataFormat format = workbook.createDataFormat();

            // 쉼표 형식 (예: 1,000)
            XSSFCellStyle commaStyle = workbook.createCellStyle();
            commaStyle.setDataFormat(format.getFormat("#,##0"));
            commaStyle.setAlignment(HorizontalAlignment.CENTER); // 가운데 정렬 추가
            // 쉼표와 퍼센트 형식 (예: 50.00%)
            XSSFCellStyle commaPercentStyle = workbook.createCellStyle();
            commaPercentStyle.setDataFormat(format.getFormat("#,##0.00%"));
            commaPercentStyle.setAlignment(HorizontalAlignment.CENTER); // 가운데 정렬 추가
            // 쉼표와 일반 소수점 형식 (예: 1,000.00)
            XSSFCellStyle commaDoubleStyle = workbook.createCellStyle();
            commaDoubleStyle.setDataFormat(format.getFormat("#,##0.00"));
            commaDoubleStyle.setAlignment(HorizontalAlignment.CENTER); // 가운데 정렬 추가
 while (resultSet.next()) {
            	
                XSSFRow dataRow = (XSSFRow) sheet5.createRow(rowNum++);
                dataRow.createCell(2).setCellValue(resultSet.getString("사이트").toString());
                dataRow.createCell(3).setCellValue(resultSet.getString("광고상품번호"));
                
               
             // 정수형에 쉼표 형식 적용
                XSSFCell cell3 = dataRow.createCell(4);
                cell3.setCellValue(resultSet.getInt("노출수"));
                cell3.setCellStyle(commaStyle);

                XSSFCell cell4 = dataRow.createCell(5);
                cell4.setCellValue(resultSet.getInt("클릭수"));
                cell4.setCellStyle(commaStyle);
               
                // 실수형에 쉼표와 퍼센트 형식 적용
                XSSFCell cell5 = dataRow.createCell(6);
                cell5.setCellValue(resultSet.getDouble("클릭률") / 100); // 값을 100으로 나눔
                cell5.setCellStyle(commaPercentStyle); // 퍼센트 형식 적용
                
                XSSFCell cell6 = dataRow.createCell(7);
                cell6.setCellValue(resultSet.getInt("평균클릭비용"));
                cell6.setCellStyle(commaStyle);

                XSSFCell cell7 = dataRow.createCell(8);
                cell7.setCellValue(resultSet.getDouble("평균노출순위"));
                cell7.setCellStyle(commaDoubleStyle); // 소수점 쉼표 형식 적용

                XSSFCell cell8 = dataRow.createCell(9);
                cell8.setCellValue(resultSet.getInt("전환수"));
                cell8.setCellStyle(commaStyle);

                XSSFCell cell9 = dataRow.createCell(10);
                cell9.setCellValue(resultSet.getInt("총비용"));
                cell9.setCellStyle(commaStyle);

                XSSFCell cell10 = dataRow.createCell(11);
                cell10.setCellValue(resultSet.getInt("전환금액"));
                cell10.setCellStyle(commaStyle);

                XSSFCell cell11 = dataRow.createCell(12);
                cell11.setCellValue(resultSet.getDouble("전환율") / 100); // 값을 100으로 나눔
                cell11.setCellStyle(commaPercentStyle); // 퍼센트 형식 적용

                XSSFCell cell12 = dataRow.createCell(13);
                cell12.setCellValue(resultSet.getDouble("광고수익률") / 100); // 값을 100으로 나눔
                cell12.setCellStyle(commaPercentStyle); // 퍼센트 형식 적용
            }
         // 데이터를 모두 추가한 후에 테두리 적용
   	        //B3에서 B499까지의 범위:
	        applyTopBorderToExistingCellsLeft(workbook, sheet5, 2, 498, 1, 1);
	        //O3에서 O499까지의 범위:
            applyTopBorderToExistingCellsRight(workbook, sheet5, 2, 498, 14, 14);
//            autoSizeColumns(sheet5, 20);
        }

        // 시트에 데이터 추가_6번_키워드별_G마켓
        private void populateSheetWithData6(XSSFSheet sheet6, ResultSet resultSet , XSSFWorkbook workbook, int startRows) throws SQLException {
            int rowNum = startRows; // 데이터 행 시작 위치
            // DataFormat 생성
            DataFormat format = workbook.createDataFormat();

            // 쉼표 형식 (예: 1,000)
            XSSFCellStyle commaStyle = workbook.createCellStyle();
            commaStyle.setDataFormat(format.getFormat("#,##0"));
            commaStyle.setAlignment(HorizontalAlignment.CENTER); // 가운데 정렬 추가
            // 쉼표와 퍼센트 형식 (예: 50.00%)
            XSSFCellStyle commaPercentStyle = workbook.createCellStyle();
            commaPercentStyle.setDataFormat(format.getFormat("#,##0.00%"));
            commaPercentStyle.setAlignment(HorizontalAlignment.CENTER); // 가운데 정렬 추가
            // 쉼표와 일반 소수점 형식 (예: 1,000.00)
            XSSFCellStyle commaDoubleStyle = workbook.createCellStyle();
            commaDoubleStyle.setDataFormat(format.getFormat("#,##0.00"));
            commaDoubleStyle.setAlignment(HorizontalAlignment.CENTER); // 가운데 정렬 추가
            while (resultSet.next()) {
                XSSFRow dataRow = (XSSFRow)sheet6.createRow(rowNum++);
                dataRow.createCell(1).setCellValue(resultSet.getString("키워드").toString());
                
             // 정수형에 쉼표 형식 적용
                XSSFCell cell2 = dataRow.createCell(3);
                cell2.setCellValue(resultSet.getInt("노출수"));
                cell2.setCellStyle(commaStyle);

                XSSFCell cell3 = dataRow.createCell(4);
                cell3.setCellValue(resultSet.getInt("클릭수"));
                cell3.setCellStyle(commaStyle);
                
                // 실수형에 쉼표와 퍼센트 형식 적용
                XSSFCell cell4 = dataRow.createCell(5);
                cell4.setCellValue(resultSet.getDouble("클릭률") / 100); // 값을 100으로 나눔
                cell4.setCellStyle(commaPercentStyle); // 퍼센트 형식 적용

                XSSFCell cell5 = dataRow.createCell(6);
                cell5.setCellValue(resultSet.getInt("평균클릭비용"));
                cell5.setCellStyle(commaStyle);

                XSSFCell cell6 = dataRow.createCell(7);
                cell6.setCellValue(resultSet.getDouble("평균노출순위"));
                cell6.setCellStyle(commaDoubleStyle); // 소수점 쉼표 형식 적용

                XSSFCell cell7 = dataRow.createCell(8);
                cell7.setCellValue(resultSet.getInt("전환수"));
                cell7.setCellStyle(commaStyle);

                XSSFCell cell8 = dataRow.createCell(9);
                cell8.setCellValue(resultSet.getInt("총비용"));
                cell8.setCellStyle(commaStyle);

                XSSFCell cell9 = dataRow.createCell(10);
                cell9.setCellValue(resultSet.getInt("전환금액"));
                cell9.setCellStyle(commaStyle);

                XSSFCell cell10 = dataRow.createCell(11);
                cell10.setCellValue(resultSet.getDouble("전환율") / 100); // 값을 100으로 나눔
                cell10.setCellStyle(commaPercentStyle); // 퍼센트 형식 적용

                XSSFCell cell11 = dataRow.createCell(12);
                cell11.setCellValue(resultSet.getDouble("광고수익률") / 100); // 값을 100으로 나눔
                cell11.setCellStyle(commaPercentStyle); // 퍼센트 형식 적용
            }
            // 데이터를 모두 추가한 후에 테두리 적용
   	        //B3에서 B499까지의 범위:
	        applyTopBorderToExistingCellsLeft(workbook, sheet6, 2, 998, 1, 1);
	        //O3에서 O499까지의 범위:
            applyTopBorderToExistingCellsRight(workbook, sheet6, 2, 998, 13, 13);
//            autoSizeColumns(sheet4, 20);
        }
        
        
        // 시트에 데이터 추가_7번_키워드별_옥션
        private void populateSheetWithData7(XSSFSheet sheet7, ResultSet resultSet , XSSFWorkbook workbook, int startRows) throws SQLException {
            int rowNum = startRows; // 데이터 행 시작 위치
         // DataFormat 생성
            DataFormat format = workbook.createDataFormat();

            // 쉼표 형식 (예: 1,000)
            XSSFCellStyle commaStyle = workbook.createCellStyle();
            commaStyle.setDataFormat(format.getFormat("#,##0"));
            commaStyle.setAlignment(HorizontalAlignment.CENTER); // 가운데 정렬 추가
            // 쉼표와 퍼센트 형식 (예: 50.00%)
            XSSFCellStyle commaPercentStyle = workbook.createCellStyle();
            commaPercentStyle.setDataFormat(format.getFormat("#,##0.00%"));
            commaPercentStyle.setAlignment(HorizontalAlignment.CENTER); // 가운데 정렬 추가
            // 쉼표와 일반 소수점 형식 (예: 1,000.00)
            XSSFCellStyle commaDoubleStyle = workbook.createCellStyle();
            commaDoubleStyle.setDataFormat(format.getFormat("#,##0.00"));
            commaDoubleStyle.setAlignment(HorizontalAlignment.CENTER); // 가운데 정렬 추가
            while (resultSet.next()) {
                XSSFRow dataRow = (XSSFRow)sheet7.createRow(rowNum++);
                dataRow.createCell(1).setCellValue(resultSet.getString("키워드").toString());
                
             // 정수형에 쉼표 형식 적용
                XSSFCell cell2 = dataRow.createCell(3);
                cell2.setCellValue(resultSet.getInt("노출수"));
                cell2.setCellStyle(commaStyle);

                XSSFCell cell3 = dataRow.createCell(4);
                cell3.setCellValue(resultSet.getInt("클릭수"));
                cell3.setCellStyle(commaStyle);
                
                // 실수형에 쉼표와 퍼센트 형식 적용
                XSSFCell cell4 = dataRow.createCell(5);
                cell4.setCellValue(resultSet.getDouble("클릭률") / 100); // 값을 100으로 나눔
                cell4.setCellStyle(commaPercentStyle); // 퍼센트 형식 적용

                XSSFCell cell5 = dataRow.createCell(6);
                cell5.setCellValue(resultSet.getInt("평균클릭비용"));
                cell5.setCellStyle(commaStyle);

                XSSFCell cell6 = dataRow.createCell(7);
                cell6.setCellValue(resultSet.getDouble("평균노출순위"));
                cell6.setCellStyle(commaDoubleStyle); // 소수점 쉼표 형식 적용

                XSSFCell cell7 = dataRow.createCell(8);
                cell7.setCellValue(resultSet.getInt("전환수"));
                cell7.setCellStyle(commaStyle);

                XSSFCell cell8 = dataRow.createCell(9);
                cell8.setCellValue(resultSet.getInt("총비용"));
                cell8.setCellStyle(commaStyle);

                XSSFCell cell9 = dataRow.createCell(10);
                cell9.setCellValue(resultSet.getInt("전환금액"));
                cell9.setCellStyle(commaStyle);

                XSSFCell cell10 = dataRow.createCell(11);
                cell10.setCellValue(resultSet.getDouble("전환율") / 100); // 값을 100으로 나눔
                cell10.setCellStyle(commaPercentStyle); // 퍼센트 형식 적용

                XSSFCell cell11 = dataRow.createCell(12);
                cell11.setCellValue(resultSet.getDouble("광고수익률") / 100); // 값을 100으로 나눔
                cell11.setCellStyle(commaPercentStyle); // 퍼센트 형식 적용
            }
            // 데이터를 모두 추가한 후에 테두리 적용
   	        //B3에서 B499까지의 범위:
	        applyTopBorderToExistingCellsLeft(workbook, sheet7, 2, 998, 1, 1);
	        //O3에서 O499까지의 범위:
            applyTopBorderToExistingCellsRight(workbook, sheet7, 2, 998, 13, 13);
//            autoSizeColumns(sheet4, 20);
        }
        

        

        public void applyTopBorderToExistingCellsTop(XSSFWorkbook workbook, XSSFSheet sheet, int startRow, int endRow, int startCol, int endCol) {
            // 테두리 스타일 생성
            XSSFCellStyle borderStyle = workbook.createCellStyle();
            borderStyle.setBorderTop(BorderStyle.THICK); // 위쪽 테두리만 설정

            // 지정된 범위 내의 모든 셀에 위쪽 테두리 적용
            for (int rowNum = startRow; rowNum <= endRow; rowNum++) {
                XSSFRow row = sheet.getRow(rowNum);
                if (row == null) {
                    row = sheet.createRow(rowNum); // 행이 없으면 새로 생성
                }

                // 각 행의 지정된 열 범위에 위쪽 테두리 스타일 적용
                for (int colNum = startCol; colNum <= endCol; colNum++) {
                    XSSFCell cell = row.getCell(colNum);
                    if (cell == null) {
                        cell = row.createCell(colNum);  // 셀이 없으면 새로 생성
                    }
                    cell.setCellStyle(borderStyle);  // 셀에 위쪽 테두리 스타일 적용
                }
            }
        }
        public void applyTopBorderToExistingCellsLeft(XSSFWorkbook workbook, XSSFSheet sheet, int startRow, int endRow, int startCol, int endCol) {
                // 테두리 스타일 생성
                XSSFCellStyle borderStyle = workbook.createCellStyle();
//                borderStyle.setBorderTop(BorderStyle.THICK); // 위쪽 테두리만 설정
                borderStyle.setBorderLeft(BorderStyle.THICK); // 왼쪽 테두리만 설정

                // 지정된 범위 내의 모든 셀에 왼쪽 테두리 적용
                for (int rowNum = startRow; rowNum <= endRow; rowNum++) {
                    XSSFRow row = sheet.getRow(rowNum);
                    if (row == null) {
                        row = sheet.createRow(rowNum); // 행이 없으면 새로 생성
                    }

                    // 각 행의 지정된 열 범위에 왼쪽 테두리 스타일 적용
                    for (int colNum = startCol; colNum <= endCol; colNum++) {
                        XSSFCell cell = row.getCell(colNum);
                        if (cell == null) {
                            cell = row.createCell(colNum);  // 셀이 없으면 새로 생성
                        }
                        cell.setCellStyle(borderStyle);  // 셀에 왼쪽 테두리 스타일 적용
                    }
                }
            }

        
        public void applyTopBorderToExistingCellsRight(XSSFWorkbook workbook, XSSFSheet sheet, int startRow, int endRow, int startCol, int endCol) {
            // 테두리 스타일 생성
            XSSFCellStyle borderStyle = workbook.createCellStyle();
//            borderStyle.setBorderTop(BorderStyle.THICK); // 위쪽 테두리만 설정
//            borderStyle.setBorderLeft(BorderStyle.THICK); // 왼쪽 테두리만 설정
            borderStyle.setBorderRight(BorderStyle.THICK); // 왼쪽 테두리만 설정

            // 지정된 범위 내의 모든 셀에 왼쪽 테두리 적용
            for (int rowNum = startRow; rowNum <= endRow; rowNum++) {
                XSSFRow row = sheet.getRow(rowNum);
                if (row == null) {
                    row = sheet.createRow(rowNum); // 행이 없으면 새로 생성
                }

                // 각 행의 지정된 열 범위에 왼쪽 테두리 스타일 적용
                for (int colNum = startCol; colNum <= endCol; colNum++) {
                    XSSFCell cell = row.getCell(colNum);
                    if (cell == null) {
                        cell = row.createCell(colNum);  // 셀이 없으면 새로 생성
                    }
                    cell.setCellStyle(borderStyle);  // 셀에 왼쪽 테두리 스타일 적용
                }
            }
        }
        
        public void applyTopBorderToExistingCellsBottom(XSSFWorkbook workbook, XSSFSheet sheet, int startRow, int endRow, int startCol, int endCol) {
            // 테두리 스타일 생성
            XSSFCellStyle borderStyle = workbook.createCellStyle();
//            borderStyle.setBorderTop(BorderStyle.THICK); // 위쪽 테두리만 설정
            borderStyle.setBorderBottom(BorderStyle.THICK); // 위쪽 테두리만 설정

            // 지정된 범위 내의 모든 셀에 위쪽 테두리 적용
            for (int rowNum = startRow; rowNum <= endRow; rowNum++) {
                XSSFRow row = sheet.getRow(rowNum);
                if (row == null) {
                    row = sheet.createRow(rowNum); // 행이 없으면 새로 생성
                }

                // 각 행의 지정된 열 범위에 위쪽 테두리 스타일 적용
                for (int colNum = startCol; colNum <= endCol; colNum++) {
                    XSSFCell cell = row.getCell(colNum);
                    if (cell == null) {
                        cell = row.createCell(colNum);  // 셀이 없으면 새로 생성
                    }
                    cell.setCellStyle(borderStyle);  // 셀에 위쪽 테두리 스타일 적용
                }
            }
        }    
        public void applyTopBorderToExistingCellsTopLeft(XSSFWorkbook workbook, XSSFSheet sheet, int startRow, int endRow, int startCol, int endCol) {
            // 테두리 스타일 생성
            XSSFCellStyle borderStyle = workbook.createCellStyle();
            borderStyle.setBorderTop(BorderStyle.THICK); // 위쪽 테두리만 설정
            borderStyle.setBorderLeft(BorderStyle.THICK); // 왼쪽 테두리만 설정

            // 지정된 범위 내의 모든 셀에 왼쪽 테두리 적용
            for (int rowNum = startRow; rowNum <= endRow; rowNum++) {
                XSSFRow row = sheet.getRow(rowNum);
                if (row == null) {
                    row = sheet.createRow(rowNum); // 행이 없으면 새로 생성
                }

                // 각 행의 지정된 열 범위에 왼쪽 테두리 스타일 적용
                for (int colNum = startCol; colNum <= endCol; colNum++) {
                    XSSFCell cell = row.getCell(colNum);
                    if (cell == null) {
                        cell = row.createCell(colNum);  // 셀이 없으면 새로 생성
                    }
                    cell.setCellStyle(borderStyle);  // 셀에 왼쪽 테두리 스타일 적용
                }
            }
        }
        public void applyTopBorderToExistingCellsTopRight(XSSFWorkbook workbook, XSSFSheet sheet, int startRow, int endRow, int startCol, int endCol) {
            // 테두리 스타일 생성
            XSSFCellStyle borderStyle = workbook.createCellStyle();
            borderStyle.setBorderTop(BorderStyle.THICK); // 위쪽 테두리만 설정
//            borderStyle.setBorderLeft(BorderStyle.THICK); // 왼쪽 테두리만 설정
            borderStyle.setBorderRight(BorderStyle.THICK); // 왼쪽 테두리만 설정

            // 지정된 범위 내의 모든 셀에 왼쪽 테두리 적용
            for (int rowNum = startRow; rowNum <= endRow; rowNum++) {
                XSSFRow row = sheet.getRow(rowNum);
                if (row == null) {
                    row = sheet.createRow(rowNum); // 행이 없으면 새로 생성
                }

                // 각 행의 지정된 열 범위에 왼쪽 테두리 스타일 적용
                for (int colNum = startCol; colNum <= endCol; colNum++) {
                    XSSFCell cell = row.getCell(colNum);
                    if (cell == null) {
                        cell = row.createCell(colNum);  // 셀이 없으면 새로 생성
                    }
                    cell.setCellStyle(borderStyle);  // 셀에 왼쪽 테두리 스타일 적용
                }
            }
        }
        public void applyTopBorderToExistingCellsLeftBottom(XSSFWorkbook workbook, XSSFSheet sheet, int startRow, int endRow, int startCol, int endCol) {
            // 테두리 스타일 생성
            XSSFCellStyle borderStyle = workbook.createCellStyle();
//            borderStyle.setBorderTop(BorderStyle.THICK); // 위쪽 테두리만 설정
            borderStyle.setBorderLeft(BorderStyle.THICK); // 왼쪽 테두리만 설정
            borderStyle.setBorderBottom(BorderStyle.THICK); // 위쪽 테두리만 설정

            // 지정된 범위 내의 모든 셀에 위쪽 테두리 적용
            for (int rowNum = startRow; rowNum <= endRow; rowNum++) {
                XSSFRow row = sheet.getRow(rowNum);
                if (row == null) {
                    row = sheet.createRow(rowNum); // 행이 없으면 새로 생성
                }

                // 각 행의 지정된 열 범위에 위쪽 테두리 스타일 적용
                for (int colNum = startCol; colNum <= endCol; colNum++) {
                    XSSFCell cell = row.getCell(colNum);
                    if (cell == null) {
                        cell = row.createCell(colNum);  // 셀이 없으면 새로 생성
                    }
                    cell.setCellStyle(borderStyle);  // 셀에 위쪽 테두리 스타일 적용
                }
            }
        }   
        
        public void applyTopBorderToExistingCellsRightBottom(XSSFWorkbook workbook, XSSFSheet sheet, int startRow, int endRow, int startCol, int endCol) {
            // 테두리 스타일 생성
            XSSFCellStyle borderStyle = workbook.createCellStyle();
//            borderStyle.setBorderTop(BorderStyle.THICK); // 위쪽 테두리만 설정
//            borderStyle.setBorderLeft(BorderStyle.THICK); // 왼쪽 테두리만 설정
            borderStyle.setBorderRight(BorderStyle.THICK); // 왼쪽 테두리만 설정
            borderStyle.setBorderBottom(BorderStyle.THICK); // 위쪽 테두리만 설정
            // 지정된 범위 내의 모든 셀에 왼쪽 테두리 적용
            for (int rowNum = startRow; rowNum <= endRow; rowNum++) {
                XSSFRow row = sheet.getRow(rowNum);
                if (row == null) {
                    row = sheet.createRow(rowNum); // 행이 없으면 새로 생성
                }

                // 각 행의 지정된 열 범위에 왼쪽 테두리 스타일 적용
                for (int colNum = startCol; colNum <= endCol; colNum++) {
                    XSSFCell cell = row.getCell(colNum);
                    if (cell == null) {
                        cell = row.createCell(colNum);  // 셀이 없으면 새로 생성
                    }
                    cell.setCellStyle(borderStyle);  // 셀에 왼쪽 테두리 스타일 적용
                }
            }
        }
        public void applyTopBorderToExistingCellsTopCenterBottom(XSSFWorkbook workbook, XSSFSheet sheet, int startRow, int endRow, int startCol, int endCol) {
        	// 지정된 범위 내의 모든 셀에 테두리 적용
            for (int rowNum = startRow; rowNum <= endRow; rowNum++) {
                XSSFRow row = sheet.getRow(rowNum);
                if (row == null) continue;

                for (int colNum = startCol; colNum <= endCol; colNum++) {
                    XSSFCell cell = row.getCell(colNum);
                    if (cell == null) {
                        cell = row.createCell(colNum);
                    }

                    // 기존 셀 스타일 복사
                    XSSFCellStyle newStyle = workbook.createCellStyle();
                    newStyle.cloneStyleFrom(cell.getCellStyle());

                    // 테두리 스타일 설정
                    newStyle.setBorderTop(BorderStyle.THIN);
                    newStyle.setBorderBottom(BorderStyle.THIN);
                    
                    

                    // 새 스타일을 셀에 적용
                    cell.setCellStyle(newStyle);
                }
            }
        }
        
//        // 자동 사이즈 맞추기   ex) autoSizeColumns(sheet1, 15);
//        public void autoSizeColumns(XSSFSheet sheet, int numberOfColumns) {
//            for (int i = 0; i < numberOfColumns; i++) {
//                sheet.autoSizeColumn(i);  // i번째 열의 너비를 자동으로 조정
//            }
//        }
//        
        

        private void setFixedColumnWidth(XSSFSheet sheet, int startColumn, int endColumn, double width) {
            int excelWidth = (int) (width * 256); // 엑셀 너비 단위로 변환 (1 단위 = 1/256)
            for (int i = startColumn; i <= endColumn; i++) {
                sheet.setColumnWidth(i, excelWidth);
            }
        }

        
        
}  // / 마지막
        
        
      
    
    	