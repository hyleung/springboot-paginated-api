package com.example.controllers.pagination;

import com.example.dao.ItemDao;
import com.example.domain.Item;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;

/**
 * Created by hyleung on 2016-08-08.
 */
public interface PaginationStrategy {
    PagedResult<Item> getPaginatedResult(ItemDao dao, Integer pageSize, Integer lastSeen) throws MalformedURLException, UnsupportedEncodingException;
}
