package com.ca.portalapi.domain;

import java.util.List;

/**
 * Created by leuho02 on 2016-08-05.
 */
public class PagedResult<T> {
    private List<T> result;
    private Integer minId;

    public PagedResult(final List<T> result) {
        this.result = result;
    }

    public List<T> getResult() {
        return result;
    }

    public void setResult(final List<T> result) {
        this.result = result;
    }

    public Integer getMinId() {
        return minId;
    }

    public void setMinId(final Integer minId) {
        this.minId = minId;
    }
}
