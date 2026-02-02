package com.example.expensetracker.service;

import com.example.expensetracker.dto.InsightsDTO;
import com.example.expensetracker.dto.RecurringExpenseDTO;
import com.example.expensetracker.models.Expense;
import com.example.expensetracker.repository.ExpenseRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FinanceAIService {

    private final ExpenseRepository repository;
    private static final double CATEGORY_PERCENT_THRESHOLD = 0.40; // 40%
    private static final double LOW_BALANCE_THRESHOLD = 500.0; // ₹500

    public FinanceAIService(ExpenseRepository repository) {
        this.repository = repository;
    }

    public InsightsDTO generateInsights(Long userId) {
        List<Expense> txns = repository.getAllExpenses(userId);

        LocalDate today = LocalDate.now();
        YearMonth thisMonth = YearMonth.from(today);
        YearMonth lastMonth = thisMonth.minusMonths(1);

        InsightsDTO out = new InsightsDTO();
        out.setWarnings(new ArrayList<>());
        out.setSuggestions(new ArrayList<>());

        double totalIncomeThis = sumForMonthAndType(txns, thisMonth, "income");
        double totalExpenseThis = sumForMonthAndType(txns, thisMonth, "expense");
        double totalIncomeLast = sumForMonthAndType(txns, lastMonth, "income");

        double balanceThisMonth = totalIncomeThis - totalExpenseThis;
        out.setBalanceThisMonth(balanceThisMonth);

        // Low-balance warning
        if (balanceThisMonth < LOW_BALANCE_THRESHOLD) {
            out.getWarnings().add("⚠️ Your balance is running low. Plan essentials carefully.");
        }

        // Category percent checks based on title or category string (if subcategory available in title)
        Map<String, Double> spendByKey = txns.stream()
                .filter(t -> "expense".equalsIgnoreCase(t.getCategory()))
                .filter(t -> YearMonth.from(t.getDate()).equals(thisMonth))
                .collect(Collectors.groupingBy(
                        t -> normalizeKey(t),
                        Collectors.summingDouble(Expense::getAmount)
                ));

        for (Map.Entry<String, Double> e : spendByKey.entrySet()) {
            double val = e.getValue();
            if (totalIncomeThis > 0 && val > CATEGORY_PERCENT_THRESHOLD * totalIncomeThis) {
                out.getSuggestions().add(
                        String.format("Consider reducing your %s expenses — they are ~%.0f%% of your income this month.",
                                e.getKey(), (val / totalIncomeThis) * 100.0)
                );
            }
        }

        // Income increase -> encourage savings
        if (totalIncomeLast > 0 && totalIncomeThis > totalIncomeLast) {
            double incPercent = (totalIncomeThis - totalIncomeLast) / totalIncomeLast * 100.0;
            if (incPercent > 5.0) {
                out.getSuggestions().add(String.format(
                        "Great! Your income increased by %.0f%% vs last month. Consider allocating some extra to savings.",
                        incPercent));
            }
        }

        // Predictions: moving average of last 3 months expenses
        List<Double> last3 = lastNMonthlyExpenses(txns, 3, thisMonth.minusMonths(1));
        double movingAvg = last3.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        double regressionPred = linearRegressionPredict(last3);
        double predictedNext = Double.isNaN(regressionPred) ? movingAvg : regressionPred;
        out.setPredictedExpenseNextMonth(predictedNext);

        // Predict end-of-month balance
        int dayOfMonth = today.getDayOfMonth();
        int daysInMonth = thisMonth.lengthOfMonth();
        double avgDailyExpenseSoFar = dayOfMonth > 0 ? totalExpenseThis / dayOfMonth : 0;
        double projectedExpense = totalExpenseThis + avgDailyExpenseSoFar * (daysInMonth - dayOfMonth);
        double predictedEndBalance = totalIncomeThis - projectedExpense;
        out.setPredictedEndOfMonthBalance(predictedEndBalance);
        if (predictedEndBalance < 0) {
            out.getWarnings().add("⚠️ Warning: at current spending rate your balance may go negative before month-end.");
        }

        // Recurring detection
        out.setRecurring(detectRecurring(txns, today));

        return out;
    }

    private double sumForMonthAndType(List<Expense> txns, YearMonth ym, String type) {
        return txns.stream()
                .filter(t -> YearMonth.from(t.getDate()).equals(ym))
                .filter(t -> type.equalsIgnoreCase(t.getCategory()))
                .mapToDouble(Expense::getAmount)
                .sum();
    }

    // Build list of last N months' expenses (ordered oldest->newest)
    private List<Double> lastNMonthlyExpenses(List<Expense> txns, int n, YearMonth endExclusive) {
        List<Double> res = new ArrayList<>();
        for (int i = n; i >= 1; i--) {
            YearMonth ym = endExclusive.minusMonths(i-1);
            double s = txns.stream()
                    .filter(t -> YearMonth.from(t.getDate()).equals(ym))
                    .filter(t -> "expense".equalsIgnoreCase(t.getCategory()))
                    .mapToDouble(Expense::getAmount).sum();
            res.add(s);
        }
        return res;
    }

    // Simple least-squares linear regression prediction for next index
    private double linearRegressionPredict(List<Double> y) {
        int n = y.size();
        if (n < 2) return Double.NaN;
        double sumX = 0, sumY = 0, sumXY = 0, sumXX = 0;
        for (int i = 0; i < n; i++) {
            double xi = i;
            double yi = y.get(i);
            sumX += xi;
            sumY += yi;
            sumXY += xi * yi;
            sumXX += xi * xi;
        }
        double denom = n * sumXX - sumX * sumX;
        if (denom == 0) return Double.NaN;
        double slope = (n * sumXY - sumX * sumY) / denom;
        double intercept = (sumY - slope * sumX) / n;
        double nextX = n; // predict for next month index
        return slope * nextX + intercept;
    }

    private List<RecurringExpenseDTO> detectRecurring(List<Expense> txns, LocalDate today) {
        YearMonth now = YearMonth.from(today);
        YearMonth sixAgo = now.minusMonths(6);

        Map<String, List<Expense>> byKey = txns.stream()
                .filter(t -> "expense".equalsIgnoreCase(t.getCategory()))
                .filter(t -> !YearMonth.from(t.getDate()).isBefore(sixAgo))
                .collect(Collectors.groupingBy(t -> normalizeKey(t)));

        List<RecurringExpenseDTO> out = new ArrayList<>();
        for (Map.Entry<String, List<Expense>> e : byKey.entrySet()) {
            List<Expense> list = e.getValue();
            Set<YearMonth> months = list.stream().map(t -> YearMonth.from(t.getDate())).collect(Collectors.toSet());
            if (months.size() >= 3) {
                double avg = list.stream().mapToDouble(Expense::getAmount).average().orElse(0.0);
                RecurringExpenseDTO r = new RecurringExpenseDTO();
                r.setTitle(e.getKey());
                r.setSubCategory(list.get(0).getCategory());
                r.setAverageAmount(avg);
                r.setFrequency("monthly");
                r.setNextExpectedDate(now.plusMonths(1).atDay(1));
                out.add(r);
            }
        }
        return out;
    }

    private String normalizeKey(Expense t) {
        if (t.getCategory() != null && !t.getCategory().isEmpty() && !t.getCategory().equalsIgnoreCase("expense") && !t.getCategory().equalsIgnoreCase("income")) {
            return t.getCategory().toLowerCase().trim();
        }
        if (t.getTitle() != null && !t.getTitle().isEmpty()) {
            String key = t.getTitle().toLowerCase().replaceAll("[^a-z0-9 ]"," ").trim();
            // take first word as simple subkey
            String[] parts = key.split("\s+");
            return parts.length > 0 ? parts[0] : key;
        }
        return "other";
    }
}
