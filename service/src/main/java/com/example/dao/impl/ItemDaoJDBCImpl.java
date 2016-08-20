package com.example.dao.impl;

import com.example.dao.ItemDao;
import com.example.domain.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created by hyleung on 2016-08-04.
 */
@Component
public class ItemDaoJDBCImpl implements ItemDao {
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public List<Item> list() {
        String query = "SELECT ID, UUID, NAME, DESCRIPTION FROM ITEMS ORDER BY ID DESC";
        return jdbcTemplate.query(query,
                (rs, rownum) -> new Item(
                        rs.getInt("ID"),
                        rs.getString("UUID"),
                        rs.getString("NAME"),
                        rs.getString("DESCRIPTION"))
        );
    }

    @Override
    public List<Item> list(final Integer pageSize, final Optional<Integer> lastSeen) {
        String query = "SELECT ID, UUID, NAME, DESCRIPTION FROM ITEMS WHERE ID < ? ORDER BY ID DESC LIMIT ?";
        return jdbcTemplate.query(query,
                (rs, rownum) -> new Item(
                        rs.getInt("ID"),
                        rs.getString("UUID"),
                        rs.getString("NAME"),
                        rs.getString("DESCRIPTION")),
                lastSeen.orElse(Integer.MAX_VALUE),
                pageSize
        );
    }

    @Override
    public Integer getMinId() {
        String minQuery = "SELECT MIN(ID) AS MIN_ID FROM ITEMS";
        return jdbcTemplate.query(minQuery,
                (rs, rowNum) -> rs.getInt("MIN_ID"))
                .stream()
                .findFirst()
                .orElseThrow(RuntimeException::new);
    }


    @Override
    public List<Item> listPrevious(final Integer pageSize, final Integer lastSeen) {
        int limit = pageSize + 1;
        String query = "SELECT ID, UUID, NAME, DESCRIPTION FROM ITEMS WHERE ID >= ? ORDER BY ID ASC LIMIT ?";
        final List<Item> items = jdbcTemplate.query(query,
                (rs, rownum) -> new Item(
                        rs.getInt("ID"),
                        rs.getString("UUID"),
                        rs.getString("NAME"),
                        rs.getString("DESCRIPTION")),
                lastSeen,
                limit);
        Collections.reverse(items);
        return items;
    }

    @Override
    public Optional<Item> get(final String uuid) {
        String query = "SELECT ID, UUID, NAME, DESCRIPTION FROM ITEMS WHERE UUID = ?";
        return jdbcTemplate.query(query,
                (rs, rownum) -> new Item(
                        rs.getInt("ID"),
                        rs.getString("UUID"),
                        rs.getString("NAME"),
                        rs.getString("DESCRIPTION")),
                uuid
        ).stream().findFirst();
    }

    @Override
    public int delete(final String uuid) {
        String command = "DELETE FROM ITEMS WHERE UUID = ?";
        return jdbcTemplate.update(command, uuid);
    }

    @Override
    public String create(final String name, final String description) {
        String uuid = UUID.randomUUID().toString();
        String command = "INSERT INTO ITEMS(UUID, NAME, DESCRIPTION) VALUES(?, ?, ?)";
        int result = jdbcTemplate.update(command, uuid, name, description);
        if (result == 0) {
            throw new RuntimeException("Ruh-roh");
        }
        return uuid;
    }
}
