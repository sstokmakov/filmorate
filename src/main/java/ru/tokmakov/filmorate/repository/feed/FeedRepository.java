package ru.tokmakov.filmorate.repository.feed;

import ru.filmorate.model.Feed;

import java.util.Collection;

public interface FeedRepository {
    Collection<Feed> getAllByUser(long userId);

    void add(Feed feed);
}
