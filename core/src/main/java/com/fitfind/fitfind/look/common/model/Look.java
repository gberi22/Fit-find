package com.fitfind.fitfind.look.common.model;

import com.fitfind.fitfind.ai.common.model.enums.Gender;
import com.fitfind.fitfind.ai.common.model.enums.Size;
import com.fitfind.fitfind.ai.common.model.enums.Style;
import com.fitfind.fitfind.client.model.Client;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CurrentTimestamp;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "looks")
@Getter
@Setter
@DynamicInsert
@AllArgsConstructor
@NoArgsConstructor
@Builder(setterPrefix = "with")
@SQLRestriction("deleted_at IS NULL")
@SequenceGenerator(name = "looks_seq", sequenceName = "looks_seq", allocationSize = 1)
public class Look {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "looks_seq")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Client client;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;

    @Enumerated(EnumType.STRING)
    private Size size;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb", nullable = false)
    private List<Style> styles;

    private BigDecimal budgetMin;

    private BigDecimal budgetMax;

    @Basic(fetch = FetchType.LAZY)
    private byte[] image;

    private String imageMimeType;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "look_products",
        joinColumns = @JoinColumn(name = "look_id"),
        inverseJoinColumns = @JoinColumn(name = "product_id")
    )
    private List<Product> products;

    @Column(nullable = false)
    private boolean isPublished;

    private LocalDateTime deletedAt;

    @CurrentTimestamp
    private LocalDateTime createdAt;
}
