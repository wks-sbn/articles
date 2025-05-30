package com.toto.model;

import java.time.LocalDateTime;

public record Article(
    String title,
    String content,
    String author,
    LocalDateTime publicationDate
) {}