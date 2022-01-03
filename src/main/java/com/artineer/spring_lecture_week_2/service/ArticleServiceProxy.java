package com.artineer.spring_lecture_week_2.service;

import com.artineer.spring_lecture_week_2.domain.Article;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class ArticleServiceProxy implements ArticleService {
    private final ArticleServiceImpl articleServiceimpl;

    @Override
    public Long save(Article request) {
        return articleServiceimpl.save(request);
    }

    @Override
    public Article findById(Long id) {
        // Pre-Process
        log.info("Request : id - {}", id);

        Article article = articleServiceimpl.findById(id);

        // Post-Process
        log.info("Response : article.title - {}, article.content - {}", article.getTitle(), article.getContent());

        return article;
    }

    @Override
    public Article update(Article request) {
        return articleServiceimpl.update(request);
    }

    @Override
    public void delete(Long id) {
        articleServiceimpl.delete(id);
    }
}