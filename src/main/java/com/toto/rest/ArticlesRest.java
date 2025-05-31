package com.toto.rest;

import java.time.Duration;

import com.toto.db.ArticlesPersister;
import com.toto.model.Article;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/articles")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ArticlesRest {

    private static final Duration TIMEOUT = Duration.ofSeconds(10);

    // Injecting the ArticlesPersister to handle database operations

    @Inject
    ArticlesPersister articlesPersister;

    @GET
    public Response getAll() {
        return articlesPersister.list()
                .onItem().transform(articles -> Response.ok(articles).build())
                .onFailure().recoverWithItem(e -> Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity("Erreur lors de la récupération des articles : " + e.getMessage())
                        .build())
                .await().atMost(TIMEOUT);
    }

    @GET
    @Path("/{title}")
    public Response getByTitle(@PathParam("title") String title) {
        return articlesPersister.getByTitle(title)
                .onItem().transform(article -> {
                    if (article == null) {
                        return Response.status(Response.Status.NOT_FOUND).build();
                    }
                    return Response.ok(article).build();
                })
                .onFailure().recoverWithItem(e -> Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity("Erreur lors de la récupération de l'article : " + e.getMessage())
                        .build())
                .await().atMost(TIMEOUT);
    }

    @POST
    public Response createArticle(Article article) {
        return articlesPersister.save(article)
                .onItem().transform(v -> Response.status(Response.Status.CREATED).build())
                .onFailure().recoverWithItem(e -> Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity("Erreur lors de la création de l'article : " + e.getMessage())
                        .build())
                .await().atMost(TIMEOUT);
    }

    @PUT
    @Path("/{title}")
    public Response updateArticle(@PathParam("title") String title, Article article) {
        return articlesPersister.update(title, article)
                .onItem().transform(updated -> {
                    if (updated == null) {
                        return Response.status(Response.Status.NOT_FOUND).build();
                    }
                    return Response.ok(updated).build();
                })
                .onFailure().recoverWithItem(e -> Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity("Erreur lors de la mise à jour de l'article : " + e.getMessage())
                        .build())
                .await().atMost(TIMEOUT);
    }
}
