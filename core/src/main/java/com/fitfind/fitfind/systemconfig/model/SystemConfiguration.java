package com.fitfind.fitfind.systemconfig.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@SequenceGenerator(name = "system_configuration_seq", sequenceName = "system_configuration_seq", allocationSize = 1)
public class SystemConfiguration {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "system_configuration_seq")
    private Long id;

    private Long tokenValidityMinutes;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
