package com.formation.blog_security.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Entity
@Table(name = "post")
public class PostEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(min = 2, max = 255, message = "Le titre doit contenir entre 2 et 255 caractères")
    @Column(nullable = false)
    private String title;

    @Column(unique = true, nullable = false)
    private String slug;

    @NotNull
    @Size(min = 10, message = "Le contenu doit contenir au moins 10 caractères")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @NotNull
    @ManyToOne(optional = false)
    private UserEntity author;
}
