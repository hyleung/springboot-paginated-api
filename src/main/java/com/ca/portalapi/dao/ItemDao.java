package com.ca.portalapi.dao;

import com.ca.portalapi.domain.Item;
import com.ca.portalapi.controllers.pagination.PagedResult;

import java.util.List;
import java.util.Optional;

/**
 * Created by leuho02 on 2016-08-04.
 */
public interface ItemDao {
    List<Item> list();
    List<Item> list(Integer pageSize, Optional<Integer> lastSeen);

    Integer getMinId();

    List<Item> listPrevious(Integer pageSize, Integer lastSeen);
    Optional<Item> get(String uuid);
    int delete(String uuid);
    String create(String name, String description);
}
