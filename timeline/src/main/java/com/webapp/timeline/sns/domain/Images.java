package com.webapp.timeline.sns.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "images")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Images {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "postId", nullable = false)
    private int postId;

    @Column(name = "thumbnail", nullable = false)
    private String thumbnail;

    @Column(name = "url", nullable = false)
    private String url;
}
