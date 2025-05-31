/**
 * Test class for ArticlesPersister.
 * This class tests the CRUD operations for articles using the ReactiveMongoClient.
 */

package com.toto.db;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.toto.model.Article;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;

@QuarkusTest
class ArticlesPersisterTest {

    @Inject
    ArticlesPersister persister;

    static final Article ARTICLE_1 = new Article(
            "First News",
            "Content of the first news.",
            "Alice",
            LocalDateTime.of(2025, 1, 1, 12, 0)
    );
    static final Article ARTICLE_2 = new Article(
            "Second News",
            "Content of the second news.",
            "Bob",
            LocalDateTime.of(2025, 2, 1, 12, 0)
    );

    @BeforeEach
    void cleanUp() {
        // Delete both articles before each test to ensure isolation
        persister.delete(ARTICLE_1.title()).await().indefinitely();
        persister.delete(ARTICLE_2.title()).await().indefinitely();
    }

    @Test
    void testSaveAndList() {
        persister.save(ARTICLE_1).await().indefinitely();
        persister.save(ARTICLE_2).await().indefinitely();

        List<Article> articles = persister.list().await().indefinitely();
        assertTrue(articles.stream().anyMatch(a -> a.title().equals(ARTICLE_1.title())));
        assertTrue(articles.stream().anyMatch(a -> a.title().equals(ARTICLE_2.title())));
    }

    @Test
    void testDelete() {
        persister.save(ARTICLE_1).await().indefinitely();
        persister.save(ARTICLE_2).await().indefinitely();

        persister.delete(ARTICLE_1.title()).await().indefinitely();
        List<Article> articles = persister.list().await().indefinitely();
        assertFalse(articles.stream().anyMatch(a -> a.title().equals(ARTICLE_1.title())));
        assertTrue(articles.stream().anyMatch(a -> a.title().equals(ARTICLE_2.title())));
    }

    @Test
    void testListEmpty() {
        List<Article> articles = persister.list().await().indefinitely();
        assertTrue(articles.isEmpty());
    }

    @Test
    void testGetByTitle() {
        persister.save(ARTICLE_1).await().indefinitely();
        Article found = persister.getByTitle(ARTICLE_1.title()).await().indefinitely();
        assertTrue(found != null && found.title().equals(ARTICLE_1.title()));

        Article notFound = persister.getByTitle("unknown-title").await().indefinitely();
        assertNull(notFound);
    }

    @Test
    void testUpdate() {
        persister.save(ARTICLE_1).await().indefinitely();
        Article updatedArticle = new Article(
            ARTICLE_1.title(),
            "Updated content",
            "Updated author",
            ARTICLE_1.publicationDate().plusDays(1)
        );
        Article updated = persister.update(ARTICLE_1.title(), updatedArticle).await().indefinitely();
        assertTrue(updated != null && updated.title().equals(ARTICLE_1.title()));

        Article fetched = persister.getByTitle(ARTICLE_1.title()).await().indefinitely();
        assertTrue(fetched != null && fetched.content().equals("Updated content"));
    }

    @Test
    void testUpdateNotFound() {
        Article updatedArticle = new Article(
            "not-exist",
            "content",
            "author",
            LocalDateTime.now()
        );
        Article updated = persister.update("not-exist", updatedArticle).await().indefinitely();
        assertNull(updated);
    }
}