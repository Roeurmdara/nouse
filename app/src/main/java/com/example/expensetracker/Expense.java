package com.example.expensetracker;

import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import java.util.Date;

public class Expense {
    @SerializedName("id")
    private String id;

    @SerializedName("amount")
    private double amount;

    @SerializedName("currency")
    private String currency;

    @SerializedName("createdDate")
    @JsonAdapter(ISO8601DateAdapter.class)
    private Date createdDate;

    @SerializedName("category")
    private String category;

    @SerializedName("remark")
    private String remark;

    @SerializedName("createdBy")
    private String createdBy;

    public Expense(String id, double amount, String currency, Date createdDate,
                   String category, String remark, String createdBy) {
        this.id = id;
        this.amount = amount;
        this.currency = currency;
        this.createdDate = createdDate;
        this.category = category;
        this.remark = remark;
        this.createdBy = createdBy;
    }

    public String getId() { return id; }
    public double getAmount() { return amount; }
    public String getCurrency() { return currency; }
    public Date getCreatedDate() { return createdDate; }
    public String getCategory() { return category; }
    public String getRemark() { return remark; }
    public String getCreatedBy() { return createdBy; }

    public void setId(String id) { this.id = id; }
    public void setAmount(double amount) { this.amount = amount; }
    public void setCurrency(String currency) { this.currency = currency; }
    public void setCreatedDate(Date createdDate) { this.createdDate = createdDate; }
    public void setCategory(String category) { this.category = category; }
    public void setRemark(String remark) { this.remark = remark; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
}