package com.example.file_upload_site.model;

import java.util.Date;

public class AdReport {
    private Date date;
    private int impressions;
    private int clicks;
    private double cost;

    // 생성자
    public AdReport(Date date, int impressions, int clicks, double cost) {
        this.date = date;
        this.impressions = impressions;
        this.clicks = clicks;
        this.cost = cost;
    }

    // Getters와 Setters
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getImpressions() {
        return impressions;
    }

    public void setImpressions(int impressions) {
        this.impressions = impressions;
    }

    public int getClicks() {
        return clicks;
    }

    public void setClicks(int clicks) {
        this.clicks = clicks;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }
}
