package com.ca.portalapi.controllers.pagination;

import com.ca.portalapi.dao.ItemDao;
import com.ca.portalapi.domain.Item;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;

/**
 * Created by leuho02 on 2016-08-08.
 */
public interface PaginationStrategy {
    PagedResult<Item> getPaginatedResult(ItemDao dao, Integer pageSize, Integer lastSeen) throws MalformedURLException, UnsupportedEncodingException;
}
