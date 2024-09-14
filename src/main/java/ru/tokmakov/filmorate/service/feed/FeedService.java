package ru.tokmakov.filmorate.service.feed;

import ru.filmorate.model.Feed;

import java.util.Collection;


public interface FeedService {
    Collection<Feed> getFeeds(long userId);
}
