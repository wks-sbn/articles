package com.toto.rest;

import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;

import com.toto.model.Article;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/articles")
public class Articles {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Article> articles() {
         return List.of(
            new Article(1L, "First News", "Content of the first news.", "Alice", LocalDateTime.of(2025, 1, 1, 12, 0)),
            new Article(2L, "Second News", "Content of the second news.", "Bob", LocalDateTime.of(2025, 2, 1, 12, 0))
        );
    }
}
