package com.project.backend.model;

import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "review_style")
public class ReviewStyle {

    @Id
    @Tsid
    // @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rstyle_id")
    private Long rstyleId;

    private String color;

    private String image;

}
