package com.ca.portalapi.controllers.pagination;

import com.ca.portalapi.dao.ItemDao;
import com.ca.portalapi.domain.Item;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by leuho02 on 2016-08-08.
 */
public class PreviousPageStrategy implements PaginationStrategy {
    @Override
    public PagedResult<Item> getPaginatedResult(final ItemDao dao, final Integer pageSize, final Integer lastSeen) throws MalformedURLException, UnsupportedEncodingException {
        List<Item> items = dao.listPrevious(pageSize, lastSeen);
        List<PagedResult.PagedResultLink> links = new ArrayList<>();
        int minId = items.get(items.size() - 1).getId();
        Integer lastSeenId = null;
        if (items.size() > pageSize) {
            lastSeenId = items.get(0).getId();
            //take just the first page
            items = items.subList(1, items.size());
        }
        links.add(new PagedResult.PagedResultLink(pageSize, minId , null, "next"));

        if (lastSeenId != null) {
            links.add(new PagedResult.PagedResultLink(pageSize, lastSeenId, "previous", "prev"));
        }
        links.add(new PagedResult.PagedResultLink(pageSize, lastSeenId, null, "self"));
        return new PagedResult<>(items, links);
    }
}
