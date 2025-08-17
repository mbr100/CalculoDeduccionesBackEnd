package com.marioborrego.api.calculodeduccionesbackend.configuration.DTO;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PageResponse<T> {
    private List<T> content;
    private int pageNumber;     // page.getNumber()
    private int pageSize;       // page.getSize()
    private long totalElements; // page.getTotalElements()
    private int totalPages;     // page.getTotalPages()
    private boolean first;      // page.isFirst()
    private boolean last;       // page.isLast()

    public static <T> PageResponse<T> from(org.springframework.data.domain.Page<T> page) {
        return PageResponse.<T>builder()
                .content(page.getContent())
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .first(page.isFirst())
                .last(page.isLast())
                .build();
    }
}
