package com.toto.db;
import java.util.List;

import org.bson.Document;

import com.toto.model.Article;

import io.quarkus.mongodb.reactive.ReactiveMongoClient;
import io.quarkus.mongodb.reactive.ReactiveMongoCollection;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class ArticlesPersister {

    private static final String DATABASE = "news";
    private static final String COLLECTION = "articles";
    
    private static final String PUBLICATION_DATE_KEY = "publicationDate";
    private static final String AUTHOR_KEY = "author";
    private static final String CONTENT_KEY = "content";
    private static final String TITLE_KEY = "title";

    @Inject
    ReactiveMongoClient mongoClient;

     private ReactiveMongoCollection<Document> getCollection() {
        return mongoClient.getDatabase(DATABASE).getCollection(COLLECTION);
    }

    //CRUD operations for articles with ReactiveMongoClient
    public Uni<List<Article>> list() {
        return getCollection().find()
            .map(doc -> new Article(
                doc.getString(TITLE_KEY),
                doc.getString(CONTENT_KEY),
                doc.getString(AUTHOR_KEY),
                doc.getDate(PUBLICATION_DATE_KEY).toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime()
            ))
            .collect().asList();
    }

    public Uni<Article> getByTitle(String title) {
    return getCollection()
        .find(new Document(TITLE_KEY, title))
        .toUni()
        .onItem().transform(doc -> {
            if (doc == null) return null;
            return new Article(
                doc.getString(TITLE_KEY),
                doc.getString(CONTENT_KEY),
                doc.getString(AUTHOR_KEY),
                doc.getDate(PUBLICATION_DATE_KEY).toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime()
            );
        });
}

    public Uni<Void> save(Article article) {
        Document doc = new Document()
            .append(TITLE_KEY, article.title())
            .append(CONTENT_KEY, article.content())
            .append(AUTHOR_KEY, article.author())
            .append(PUBLICATION_DATE_KEY, java.util.Date.from(article.publicationDate().atZone(java.time.ZoneId.systemDefault()).toInstant()));

        return getCollection()
            .insertOne(doc)
            .onItem().ignore().andContinueWithNull();
    }

    public Uni<Void> delete(String title) {
        return getCollection()
            .deleteOne(new Document(TITLE_KEY, title))
            .onItem().ignore().andContinueWithNull();
    }

    public Uni<Article> update(String title, Article article) {
    Document filter = new Document(TITLE_KEY, title);
    Document update = new Document("$set", new Document()
        .append(CONTENT_KEY, article.content())
        .append(AUTHOR_KEY, article.author())
        .append(PUBLICATION_DATE_KEY, java.util.Date.from(article.publicationDate().atZone(java.time.ZoneId.systemDefault()).toInstant()))
    );
    return getCollection()
        .findOneAndUpdate(filter, update)
        .onItem().transform(doc -> {
            if (doc == null) return null;
            return new Article(
                doc.getString(TITLE_KEY),
                doc.getString(CONTENT_KEY),
                doc.getString(AUTHOR_KEY),
                doc.getDate(PUBLICATION_DATE_KEY).toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime()
            );
        });
}
}