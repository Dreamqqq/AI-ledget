package com.jizhang.ledger.model;

public class TransactionRequest {
    private String type;
    private String category;
    private Double amount;
    private String date;
    private String remark;
    private String imageUrl;

    public TransactionRequest(String type, String category, Double amount, String date, String remark) {
        this.type = type;
        this.category = category;
        this.amount = amount;
        this.date = date;
        this.remark = remark;
    }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}
