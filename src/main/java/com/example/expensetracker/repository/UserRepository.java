package com.example.expensetracker.repository;

import com.example.expensetracker.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // SIGNUP
    public int save(User user) {
        String sql = "INSERT INTO users(username, password) VALUES (?, ?)";
        return jdbcTemplate.update(sql, user.getUsername(), user.getPassword());
    }

    // LOGIN
    public User findByUsername(String username) {
    String sql = "SELECT * FROM users WHERE username = ?";
    return jdbcTemplate.queryForObject(sql,
            new BeanPropertyRowMapper<>(User.class),
            username);
}


    // FIND BY ID
    public User findById(Long id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(User.class), id);
    }
}
