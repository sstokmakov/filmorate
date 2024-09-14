package ru.tokmakov.filmorate.service.feed;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.filmorate.exception.NotFoundException;
import ru.filmorate.model.Feed;
import ru.filmorate.repository.feed.FeedRepository;
import ru.filmorate.repository.user.UserRepository;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class FeedServiceImpl implements FeedService {
    private final FeedRepository feedRepository;
    private final UserRepository userRepository;

    @Override
    public Collection<Feed> getFeeds(long userId) {
        userRepository.getById(userId).orElseThrow(() ->
                new NotFoundException("Not found feeds by user. User with id " + userId + " not found"));
        return feedRepository.getAllByUser(userId);
    }
}
