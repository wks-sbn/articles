package com.toto.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.toto.db.ArticlesPersister;
import com.toto.model.Article;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;

@QuarkusTest
class ArticlesRestTest {
    
    @Inject
    ArticlesRest resource;

    @InjectMock
    ArticlesPersister persister;

    @Test
    void testGetAllSuccess() {
        List<Article> articles = Arrays.asList(
                new Article("t1", "c1", "a1", LocalDateTime.now()),
                new Article("t2", "c2", "a2", LocalDateTime.now())
        );
        when(persister.list()).thenReturn(Uni.createFrom().item(articles));

        Response resp = resource.getAll();
        assertEquals(Response.Status.OK.getStatusCode(), resp.getStatus());
        assertEquals(articles, resp.getEntity());
    }

    @Test
    void testGetAllFailure() {
        when(persister.list()).thenReturn(Uni.createFrom().failure(new RuntimeException("fail")));
        Response resp = resource.getAll();
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), resp.getStatus());
        assertTrue(resp.getEntity().toString().contains("fail"));
    }

    @Test
    void testGetByTitleFound() {
        Article article = new Article("t1", "c1", "a1", LocalDateTime.now());
        when(persister.getByTitle("t1")).thenReturn(Uni.createFrom().item(article));
        Response resp = resource.getByTitle("t1");
        assertEquals(Response.Status.OK.getStatusCode(), resp.getStatus());
        assertEquals(article, resp.getEntity());
    }

    @Test
    void testGetByTitleNotFound() {
        when(persister.getByTitle("t1")).thenReturn(Uni.createFrom().item((Article) null));
        Response resp = resource.getByTitle("t1");
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), resp.getStatus());
    }

    @Test
    void testGetByTitleFailure() {
        when(persister.getByTitle("t1")).thenReturn(Uni.createFrom().failure(new RuntimeException("fail")));
        Response resp = resource.getByTitle("t1");
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), resp.getStatus());
        assertTrue(resp.getEntity().toString().contains("fail"));
    }

    @Test
    void testCreateArticleSuccess() {
        Article article = new Article("t1", "c1", "a1", LocalDateTime.now());
        when(persister.save(article)).thenReturn(Uni.createFrom().nullItem());
        Response resp = resource.createArticle(article);
        assertEquals(Response.Status.CREATED.getStatusCode(), resp.getStatus());
    }

    @Test
    void testCreateArticleFailure() {
        Article article = new Article("t1", "c1", "a1", LocalDateTime.now());
        when(persister.save(article)).thenReturn(Uni.createFrom().failure(new RuntimeException("fail")));
        Response resp = resource.createArticle(article);
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), resp.getStatus());
        assertTrue(resp.getEntity().toString().contains("fail"));
    }

    @Test
    void testUpdateArticleSuccess() {
        Article article = new Article("t1", "c1", "a1", LocalDateTime.now());
        when(persister.update("t1", article)).thenReturn(Uni.createFrom().item(article));
        Response resp = resource.updateArticle("t1", article);
        assertEquals(Response.Status.OK.getStatusCode(), resp.getStatus());
        assertEquals(article, resp.getEntity());
    }

    @Test
    void testUpdateArticleNotFound() {
        Article article = new Article("t1", "c1", "a1", LocalDateTime.now());
        when(persister.update("t1", article)).thenReturn(Uni.createFrom().item((Article) null));
        Response resp = resource.updateArticle("t1", article);
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), resp.getStatus());
    }

    @Test
    void testUpdateArticleFailure() {
        Article article = new Article("t1", "c1", "a1", LocalDateTime.now());
        when(persister.update("t1", article)).thenReturn(Uni.createFrom().failure(new RuntimeException("fail")));
        Response resp = resource.updateArticle("t1", article);
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), resp.getStatus());
        assertTrue(resp.getEntity().toString().contains("fail"));
    }
}
