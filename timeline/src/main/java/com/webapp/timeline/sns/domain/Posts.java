package com.webapp.timeline.sns.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.webapp.timeline.sns.web.json.CustomPostDeserializer;
import com.webapp.timeline.sns.web.json.CustomPostSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Formula;

import javax.persistence.*;
import java.sql.Timestamp;


@Entity
@Table(name = "posts")
@JsonSerialize(using = CustomPostSerializer.class)
@JsonDeserialize(using = CustomPostDeserializer.class)
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Posts {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int postId;

    @Column(name = "author", nullable = false)
    private String author;

    @Column(name = "content", nullable = false)
    private String content;

    @Convert(converter = ConverterShowLevel.class)
    @Column(name = "showLevel", nullable = false)
    private String showLevel;

    @Column(name = "lastUpdate", nullable = false)
    private Timestamp lastUpdate;

    @Column(name = "deleted")
    private int deleted;

    @Formula("(select count(*) from comments c where c.postId = postId AND c.deleted = 0)")
    private Long commentNum;

    public void setContent(String content) {
        this.content = content;
    }

    public void setShowLevel(String showLevel) {
        this.showLevel = showLevel;
    }

    public void setLastUpdate(Timestamp lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public void setDeleted(int deleted) {
        this.deleted = deleted;
    }
}
