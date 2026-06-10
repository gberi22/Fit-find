package com.fitfind.fitfind.look.common.model.response;

import java.time.LocalDateTime;
import java.util.UUID;

public interface LookSummaryProjection {

    Long getId();

    UUID getImageKey();

    boolean getIsPublished();

    LocalDateTime getCreatedAt();
}
