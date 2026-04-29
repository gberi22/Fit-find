package com.fitfind.fitfind.model.requests;

import lombok.*;
import org.hibernate.annotations.DynamicInsert;

@Getter
@Setter
@DynamicInsert
@AllArgsConstructor
@NoArgsConstructor
@Builder(setterPrefix = "with")
public class RegisterRequest {
    private String email;

    private String password;

    private String firstName;

    private String lastName;
}
