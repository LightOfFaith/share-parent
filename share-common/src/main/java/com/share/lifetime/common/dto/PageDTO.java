package com.share.lifetime.common.dto;

import java.util.Collections;
import java.util.List;

public class PageDTO<T> {

    private final long total;
    private final List<T> content;
    private final int page;
    private final int size;

    public PageDTO(long total, List<T> content, int page, int size) {
        this.total = total;
        this.content = content;
        this.page = page;
        this.size = size;
    }

    public long getTotal() {
        return total;
    }

    public List<T> getContent() {
        return Collections.unmodifiableList(content);
    }

    public int getPage() {
        return page;
    }

    public int getSize() {
        return size;
    }

    public boolean hasContent() {
        return content != null && content.size() > 0;
    }

}
