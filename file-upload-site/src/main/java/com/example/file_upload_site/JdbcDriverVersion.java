//package com.example.file_upload_site;
//
//
//import java.sql.Connection;
//import java.sql.DatabaseMetaData;
//import java.sql.DriverManager;
//import java.sql.SQLException;
//
//public class JdbcDriverVersion {
//    public static void main(String[] args) {
//        try {
//            Connection connection = DriverManager.getConnection(
//                "jdbc:sqlserver://localhost:1433;databaseName=UpLoadHm;encrypt=false", 
//                "sa", 
//                "1234"
//            );
//            DatabaseMetaData metaData = connection.getMetaData();
//            System.out.println("JDBC Driver Name: " + metaData.getDriverName());
//            System.out.println("JDBC Driver Version: " + metaData.getDriverVersion());
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }
//}
