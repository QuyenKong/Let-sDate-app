package com.example.sem6.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper=false)
@AllArgsConstructor
@NoArgsConstructor
public class PagedResponse<T> extends RestResponse<T> {
    private int pageNumber;
    private int pageSize;
    private int totalPages;
    private int totalRecords;
}
