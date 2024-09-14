package ru.tokmakov.filmorate.service.review;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.filmorate.exception.NotFoundException;
import ru.filmorate.model.EventOperation;
import ru.filmorate.model.EventType;
import ru.filmorate.model.Feed;
import ru.filmorate.model.Review;
import ru.filmorate.repository.feed.FeedRepository;
import ru.filmorate.repository.film.FilmRepository;
import ru.filmorate.repository.review.ReviewRepository;
import ru.filmorate.repository.user.UserRepository;
import ru.filmorate.service.validation.ValidationService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;
    private final ValidationService validationService;
    private final UserRepository userRepository;
    private final FilmRepository filmRepository;
    private final FeedRepository feedRepository;

    @Override
    public Review create(Review review) {
        validationService.validateNewData(review);
        checkUser(review.getUserId());
        checkUser(review.getFilmId());
        Review reviewWithId = reviewRepository.create(review);

        Feed feed = new Feed();
        feed.setEventType(EventType.REVIEW);
        feed.setOperation(EventOperation.ADD);
        feed.setUserId(review.getUserId());
        feed.setEntityId(reviewWithId.getId());
        feedRepository.add(feed);
        return review;
    }

    @Override
    public Review update(Review review) {
        validationService.validateNewData(review);
        checkUser(review.getUserId());
        checkFim(review.getFilmId());
        Review update = reviewRepository.update(review);

        Feed feed = new Feed();
        feed.setEventType(EventType.REVIEW);
        feed.setOperation(EventOperation.UPDATE);
        feed.setUserId(update.getUserId());
        feed.setEntityId(update.getId());
        feedRepository.add(feed);
        return update;
    }

    @Override
    public void delete(Long id) {
        Review review = getById(id);
        Feed feed = new Feed();
        feed.setEventType(EventType.REVIEW);
        feed.setOperation(EventOperation.REMOVE);
        feed.setUserId(review.getUserId());
        feed.setEntityId(review.getId());
        feedRepository.add(feed);
        reviewRepository.delete(id);
    }

    @Override
    public Review getById(Long id) {
        return reviewRepository.getById(id)
                .orElseThrow(() -> new NotFoundException("Not found review with id: " + id));
    }

    @Override
    public List<Review> getAll(Long filmId, int count) {
        return reviewRepository.getByFilmLimit(filmId, count);
    }

    @Override
    public List<Review> getAll(int count) {
        return reviewRepository.getByFilmLimit(count);
    }

    @Override
    public void addLikeOrDislike(Long id, Long userId, boolean like) {
        checkUser(userId);
        reviewRepository.addLikeOrDisLike(id, userId, like);
    }


    @Override
    public void removeLikeOrDislike(Long id, Long userId) {
        checkUser(userId);
        reviewRepository.removeLikeOrDislike(id, userId);
    }

    private void checkUser(Long userId) {
        userRepository.getById(userId)
                .orElseThrow(() -> new NotFoundException("Not found user with id: " + userId));
    }

    private void checkFim(Long userId) {
        filmRepository.getById(userId)
                .orElseThrow(() -> new NotFoundException("Not found film with id: " + userId));
    }
}
