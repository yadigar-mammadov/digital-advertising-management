package com.digitaladvertisingmanagement.shared.pagination;

public record PageRequestData(int page, int size) {
  public PageRequestData {
    if (page < 0) {
      throw new IllegalArgumentException("Page must be greater than or equal to 0");
    }

    if (size <= 0 || size > 100) {
      throw new IllegalArgumentException("Size must be between 1 and 100");
    }
  }
}
