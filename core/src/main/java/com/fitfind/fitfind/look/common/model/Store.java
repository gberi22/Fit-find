package com.fitfind.fitfind.look.common.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;

@Entity
@Table(name = "stores")
@Getter
@Setter
@DynamicInsert
@AllArgsConstructor
@NoArgsConstructor
@Builder(setterPrefix = "with")
@SequenceGenerator(name = "stores_seq", sequenceName = "stores_seq", allocationSize = 1)
public class Store {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "stores_seq")
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    private String url;
}
