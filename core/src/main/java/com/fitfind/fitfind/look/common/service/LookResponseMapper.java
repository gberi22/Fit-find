package com.fitfind.fitfind.look.common.service;

import com.fitfind.fitfind.client.model.Client;
import com.fitfind.fitfind.look.common.model.Look;
import com.fitfind.fitfind.look.common.model.response.LookDetailResponse;
import com.fitfind.fitfind.look.common.model.response.LookSummaryProjection;
import com.fitfind.fitfind.look.common.model.response.LookSummaryResponse;
import com.fitfind.fitfind.look.common.model.response.ProductResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LookResponseMapper {

    private static final String IMAGE_URL_PREFIX = "/api/public/look-images/";

    public LookSummaryResponse SummaryProjectionToSummaryResponse(LookSummaryProjection projection) {
        return new LookSummaryResponse(
            projection.getId(),
            IMAGE_URL_PREFIX + projection.getImageKey(),
            projection.getPublished()
        );
    }

    public LookSummaryResponse SummaryProjectionToSummaryResponse(Look look) {
        return new LookSummaryResponse(
            look.getId(),
            IMAGE_URL_PREFIX + look.getImageKey(),
            look.getPublishedAt() != null
        );
    }

    public LookDetailResponse lookToDetailResponse(Look look) {
        Client owner = look.getClient();
        List<ProductResponse> products = look.getProducts().stream()
            .map(product -> new ProductResponse(
                product.getName(),
                product.getUrl(),
                product.getPrice(),
                product.getCategory(),
                product.getImageUrl()
            ))
            .toList();

        return new LookDetailResponse(
            look.getId(),
            IMAGE_URL_PREFIX + look.getImageKey(),
            buildUsername(owner.getFirstName(), owner.getLastName()),
            look.getStyles(),
            look.getGender(),
            look.getSize(),
            look.getBudgetMin(),
            look.getBudgetMax(),
            look.getPublishedAt() != null,
            look.getCreatedAt(),
            look.getPublishedAt(),
            products
        );
    }

    private String buildUsername(String firstName, String lastName) {
        return ((firstName == null ? "" : firstName) + " " + (lastName == null ? "" : lastName)).trim();
    }
}
