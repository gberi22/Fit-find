package com.fitfind.fitfind.look.common.model.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface LookCardProjection {
    Long getId();

    String getGender();

    BigDecimal getBudgetMin();

    BigDecimal getBudgetMax();

    String getStyles();

    String getImageMimeType();

    String getFirstName();

    String getLastName();

    LocalDateTime getCreatedAt();
}
