package com.artineer.spring_lecture_week_2.domain;

import com.artineer.spring_lecture_week_2.dto.ArticleDto;
import lombok.*;
import org.springframework.data.redis.core.RedisHash;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@RedisHash
public class Article {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String title;
    String content;

    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public static Article of(ArticleDto.ReqPost from) {
        return Article.builder()
                .title(from.getTitle())
                .content(from.getContent())
                .build();
    }

    public static Article of(ArticleDto.ReqPut from, Long id) {
        return Article.builder()
                .id(id)
                .title(from.getTitle())
                .content(from.getContent())
                .build();
    }
}
