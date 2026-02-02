package com.example.adminservice.repository;

import com.example.adminservice.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<User> findAll() {
        String sql = "SELECT * FROM users";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(User.class));
    }

    public User findById(Long id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(User.class), id);
    }

    // update email if column exists, update username fallback; update is_active if provided
    public int updateUser(Long id, String email, Boolean isActive) {
        int rows = 0;
        // Try update email column
        try {
            if (email != null) {
                String sql = "UPDATE users SET email = ? WHERE id = ?";
                rows += jdbcTemplate.update(sql, email, id);
            }
        } catch (Exception e) {
            // if email column doesn't exist, try updating username
            try {
                if (email != null) {
                    String sql2 = "UPDATE users SET username = ? WHERE id = ?";
                    rows += jdbcTemplate.update(sql2, email, id);
                }
            } catch (Exception ex) {
                // ignore
            }
        }

        // Update is_active column if provided
        if (isActive != null) {
            try {
                String sql3 = "UPDATE users SET is_active = ? WHERE id = ?";
                rows += jdbcTemplate.update(sql3, isActive, id);
            } catch (Exception e) {
                // if column name different or not present, ignore for now
            }
        }
        return rows;
    }

    public int deleteUser(Long id) {
        String sql = "DELETE FROM users WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }

    public int setActive(Long id, boolean active) {
        String sql = "UPDATE users SET is_active = ? WHERE id = ?";
        return jdbcTemplate.update(sql, active, id);
    }
}
