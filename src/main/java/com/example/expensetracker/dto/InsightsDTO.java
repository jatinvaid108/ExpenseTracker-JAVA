package com.example.expensetracker.dto;

import java.util.List;

public class InsightsDTO {
    private Double balanceThisMonth;
    private List<String> warnings;
    private List<String> suggestions;
    private Double predictedExpenseNextMonth;
    private Double predictedEndOfMonthBalance;
    private List<RecurringExpenseDTO> recurring;

    public InsightsDTO() {}

    // getters and setters
    public Double getBalanceThisMonth() { return balanceThisMonth; }
    public void setBalanceThisMonth(Double balanceThisMonth) { this.balanceThisMonth = balanceThisMonth; }
    public List<String> getWarnings() { return warnings; }
    public void setWarnings(List<String> warnings) { this.warnings = warnings; }
    public List<String> getSuggestions() { return suggestions; }
    public void setSuggestions(List<String> suggestions) { this.suggestions = suggestions; }
    public Double getPredictedExpenseNextMonth() { return predictedExpenseNextMonth; }
    public void setPredictedExpenseNextMonth(Double predictedExpenseNextMonth) { this.predictedExpenseNextMonth = predictedExpenseNextMonth; }
    public Double getPredictedEndOfMonthBalance() { return predictedEndOfMonthBalance; }
    public void setPredictedEndOfMonthBalance(Double predictedEndOfMonthBalance) { this.predictedEndOfMonthBalance = predictedEndOfMonthBalance; }
    public List<RecurringExpenseDTO> getRecurring() { return recurring; }
    public void setRecurring(List<RecurringExpenseDTO> recurring) { this.recurring = recurring; }
}
