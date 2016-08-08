package com.ca.portalapi.domain;

import java.util.List;

/**
 * Created by leuho02 on 2016-08-05.
 */
public class PagedResult<T> {
    private List<T> result;
    private boolean lastPage;
    private Integer lastSeen;

    public PagedResult(final List<T> result, final Integer lastSeen, final boolean lastPage) {
        this.result = result;
        this.lastSeen = lastSeen;
        this.lastPage = lastPage;
    }

    public List<T> getResult() {
        return result;
    }

    public Integer getLastSeen() {
        return lastSeen;
    }

    public boolean isLastPage() {
        return lastPage;
    }
}
