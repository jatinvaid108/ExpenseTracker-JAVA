package com.example.adminservice.repository;

import com.example.adminservice.models.Expense;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ExpenseRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<Expense> findByUserId(Long userId) {
        String sql = "SELECT * FROM expenses WHERE user_id = ?";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Expense.class), userId);
    }

    public Expense findById(Long id) {
        String sql = "SELECT * FROM expenses WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(Expense.class), id);
    }

    public int updateExpense(Long id, Expense e) {
        String sql = "UPDATE expenses SET title = ?, amount = ?, category = ?, date = ? WHERE id = ?";
        return jdbcTemplate.update(sql, e.getTitle(), e.getAmount(), e.getCategory(), e.getDate(), id);
    }

    public int deleteExpense(Long id) {
        String sql = "DELETE FROM expenses WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }
}
