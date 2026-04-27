package com.fitfind.fitfind.model.requests;

import lombok.*;
import org.hibernate.annotations.DynamicInsert;

@Getter
@Setter
@DynamicInsert
@AllArgsConstructor
@NoArgsConstructor
@Builder(setterPrefix = "with")
public class LoginRequest {
    private String email;

    private String password;
}
