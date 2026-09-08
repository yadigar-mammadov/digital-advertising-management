package com.digitaladvertisingmanagement.shared.pagination;

import java.util.List;
import java.util.function.Function;

public record PageResult<T>(List<T> items, long totalItems, int totalPages, int page, int size) {
  public <R> PageResult<R> map(Function<T, R> mapper) {
    return new PageResult<>(
        items.stream().map(mapper).toList(), totalItems, totalPages, page, size);
  }
}
