package com.ca.portalapi.dao.impl;

import com.ca.portalapi.dao.ItemDao;
import com.ca.portalapi.domain.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by leuho02 on 2016-08-04.
 */
@Component
public class ItemDaoJDBCImpl implements ItemDao {
    @Autowired
    JdbcTemplate jdbcTemplate;
    @Override
    public List<Item> list() {
        String query = "SELECT ID, UUID, NAME, DESCRIPTION FROM ITEMS";
        return jdbcTemplate.query(query,
                (rs, rownum) -> new Item(
                        rs.getInt("ID"),
                        rs.getString("UUID"),
                        rs.getString("NAME"),
                        rs.getString("DESCRIPTION"))
                );
    }

    @Override
    public Item get(final String uuid) {
        return null;
    }

    @Override
    public void delete(final String uuid) {

    }

    @Override
    public String create(final Item item) {
        return null;
    }
}
