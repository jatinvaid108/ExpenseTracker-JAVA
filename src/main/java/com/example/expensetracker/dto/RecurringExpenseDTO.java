package com.example.expensetracker.dto;

import java.time.LocalDate;

public class RecurringExpenseDTO {
    private String title;
    private String subCategory;
    private Double averageAmount;
    private String frequency;
    private LocalDate nextExpectedDate;

    public RecurringExpenseDTO() {}

    // getters and setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getSubCategory() { return subCategory; }
    public void setSubCategory(String subCategory) { this.subCategory = subCategory; }
    public Double getAverageAmount() { return averageAmount; }
    public void setAverageAmount(Double averageAmount) { this.averageAmount = averageAmount; }
    public String getFrequency() { return frequency; }
    public void setFrequency(String frequency) { this.frequency = frequency; }
    public LocalDate getNextExpectedDate() { return nextExpectedDate; }
    public void setNextExpectedDate(LocalDate nextExpectedDate) { this.nextExpectedDate = nextExpectedDate; }
}
