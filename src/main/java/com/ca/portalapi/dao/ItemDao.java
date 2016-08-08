package com.ca.portalapi.dao;

import com.ca.portalapi.domain.Item;

import java.util.List;
import java.util.Optional;

/**
 * Created by leuho02 on 2016-08-04.
 */
public interface ItemDao {
    List<Item> list();
    Optional<Item> get(String uuid);
    int delete(String uuid);
    String create(Item item);
}
