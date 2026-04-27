package com.fitfind.fitfind.security.exception.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(setterPrefix = "with")
public class ApiErrors {

    private String message;
}
