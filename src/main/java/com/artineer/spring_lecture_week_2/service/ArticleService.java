package com.artineer.spring_lecture_week_2.service;

import com.artineer.spring_lecture_week_2.domain.Article;

public interface ArticleService {
    // CRUD에 해당하는 시그니처
    Long save(Article request);

    Article findById(Long id);

    Article update(Article request);

    void delete(Long id);
}
