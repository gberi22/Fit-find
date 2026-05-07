package com.fitfind.fitfind.ai.model;

import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@Getter
@Setter
public class Prompt {

    private List<Clothes> clothes = new ArrayList<>();
    private List<Style> styles = new ArrayList<>();
    private BigDecimal minPrice = BigDecimal.ZERO;
    private BigDecimal maxPrice = new BigDecimal("100.00");
    private String additionalComments = "";

}
