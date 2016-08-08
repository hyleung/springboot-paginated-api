package com.ca.portalapi.dao;

import com.ca.portalapi.domain.Item;

import java.util.List;

/**
 * Created by leuho02 on 2016-08-04.
 */
public interface ItemDao {
    List<Item> list();
    Item get(String uuid);
    void delete(String uuid);
    String create(Item item);
}
