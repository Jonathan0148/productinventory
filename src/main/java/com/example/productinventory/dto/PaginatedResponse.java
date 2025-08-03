package com.example.productinventory.dto;

import java.util.List;

public class PaginatedResponse<T> {

    private List<T> data;
    private Meta meta;

    public PaginatedResponse(List<T> data, Meta meta) {
        this.data = data;
        this.meta = meta;
    }

    public List<T> getData() {
        return data;
    }

    public Meta getMeta() {
        return meta;
    }

    public static class Meta {
        private int page;
        private int size;
        private int totalPages;
        private long totalElements;

        public Meta(int page, int size, int totalPages, long totalElements) {
            this.page = page;
            this.size = size;
            this.totalPages = totalPages;
            this.totalElements = totalElements;
        }

        public int getPage() {
            return page;
        }

        public int getSize() {
            return size;
        }

        public int getTotalPages() {
            return totalPages;
        }

        public long getTotalElements() {
            return totalElements;
        }
    }
}
