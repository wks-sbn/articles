package com.toto.rest;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import com.toto.model.Article;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class ArticlesTest {
    
    @Test
    void testArticleEndpoint() {
         Article[] articles = given()
            .when().get("/articles")
            .then()
            .statusCode(200)
            .extract().body().as(Article[].class);

        assertEquals(2, articles.length);

        Article first = articles[0];
        assertEquals(1L, first.id());
        assertEquals("First News", first.title());
        assertEquals("Content of the first news.", first.content());
        assertEquals("Alice", first.author());
        assertEquals(LocalDateTime.of(2025, 1, 1, 12, 0), first.publicationDate());

        Article second = articles[1];
        assertEquals(2L, second.id());
        assertEquals("Second News", second.title());
        assertEquals("Content of the second news.", second.content());
        assertEquals("Bob", second.author());
        assertEquals(LocalDateTime.of(2025, 2, 1, 12, 0), second.publicationDate());
    }
}
