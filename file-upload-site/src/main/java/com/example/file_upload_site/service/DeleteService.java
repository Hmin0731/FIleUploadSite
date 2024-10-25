package com.example.file_upload_site.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class DeleteService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void deleteDataByAdvertiserId(String advertiserId) {
    	 String sql1 = "DELETE FROM GmarketProductReport WHERE AdvertiserId = ?";
         String sql2 = "DELETE FROM GmarketDatewiseReport WHERE AdvertiserId = ?";
         String sql3 = "DELETE FROM GmarketKeywordReport WHERE AdvertiserId = ?";
         String sql4 = "DELETE FROM AuctionProductReport WHERE AdvertiserId = ?";
         String sql5 = "DELETE FROM AuctionDatewiseReport WHERE AdvertiserId = ?";
         String sql6 = "DELETE FROM AuctionKeywordReport WHERE AdvertiserId = ?";

         // 각 쿼리를 순차적으로 실행합니다.
         jdbcTemplate.update(sql1, advertiserId);
         jdbcTemplate.update(sql2, advertiserId);
         jdbcTemplate.update(sql3, advertiserId);
         jdbcTemplate.update(sql4, advertiserId);
         jdbcTemplate.update(sql5, advertiserId);
         jdbcTemplate.update(sql6, advertiserId);
    }
}
