package com.example.dao;

import com.example.domain.Item;

import java.util.List;
import java.util.Optional;

/**
 * Created by hyleung on 2016-08-04.
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
