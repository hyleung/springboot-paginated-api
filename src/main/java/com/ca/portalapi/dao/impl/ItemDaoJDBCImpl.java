package com.ca.portalapi.dao.impl;

import com.ca.portalapi.dao.ItemDao;
import com.ca.portalapi.domain.Item;
import com.ca.portalapi.domain.PagedResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Created by leuho02 on 2016-08-04.
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
    public PagedResult<Item> list(final Integer pageSize, final Optional<Integer> lastSeen) {
        final List<Item> items;
        final Integer maxId = lastSeen.orElse(Integer.MAX_VALUE);
        String query = "SELECT ID, UUID, NAME, DESCRIPTION FROM ITEMS WHERE ID < ? ORDER BY ID DESC LIMIT ?";
        items = jdbcTemplate.query(query,
                (rs, rownum) -> new Item(
                        rs.getInt("ID"),
                        rs.getString("UUID"),
                        rs.getString("NAME"),
                        rs.getString("DESCRIPTION")),
                maxId,
                pageSize
        );

        String minQuery = "SELECT MIN(ID) AS MIN_ID FROM ITEMS";
        final Integer minId = jdbcTemplate.query(minQuery,
                (rs, rowNum) -> rs.getInt("MIN_ID"))
                .stream()
                .findFirst()
                .orElseThrow(RuntimeException::new);

        //compute lastSeenId
        final Integer lastSeenId = items.stream()
                .min(Comparator.comparingInt(Item::getId))
                .map(Item::getId)
                .orElseThrow(RuntimeException::new);

        final PagedResult<Item> result = new PagedResult<>(items,lastSeenId, minId == lastSeenId);
        return  result;
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
        return null;
    }
}
