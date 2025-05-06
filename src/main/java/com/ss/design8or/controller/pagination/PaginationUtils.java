package com.ss.design8or.controller.pagination;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;

public class PaginationUtils {

    public static HttpHeaders getPaginationHeaders(Page<?> page) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(PaginationHeaders.TOTAL_COUNT, String.valueOf(page.getTotalElements()));
        headers.add(PaginationHeaders.PAGE_NUMBER, String.valueOf(page.getNumber()));
        headers.add(PaginationHeaders.PAGE_SIZE, String.valueOf(page.getSize()));
        headers.add(PaginationHeaders.TOTAL_PAGES, String.valueOf(page.getTotalPages()));
        return headers;
    }
}
