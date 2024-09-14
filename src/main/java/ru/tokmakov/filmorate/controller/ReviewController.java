package ru.tokmakov.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.filmorate.model.Review;
import ru.filmorate.service.review.ReviewService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping
    public Review create(@RequestBody Review review) {
        return reviewService.create(review);
    }

    @PutMapping
    public Review update(@RequestBody Review review) {
        return reviewService.update(review);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        reviewService.delete(id);
    }

    @GetMapping("/{id}")
    public Review getById(@PathVariable Long id) {
        return reviewService.getById(id);
    }

    @GetMapping
    public List<Review> getAll(@RequestParam Optional<Long> filmId, @RequestParam(defaultValue = "10") int count) {
        if (filmId.isPresent()) {
            return reviewService.getAll(filmId.get(), count);
        } else {
            return reviewService.getAll(count);
        }
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable Long id, @PathVariable Long userId) {
        reviewService.addLikeOrDislike(id, userId, true);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public void addDislike(@PathVariable Long id, @PathVariable Long userId) {
        reviewService.addLikeOrDislike(id, userId, false);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable Long id, @PathVariable Long userId) {
        reviewService.removeLikeOrDislike(id, userId);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public void removeDislike(@PathVariable Long id, @PathVariable Long userId) {
        reviewService.removeLikeOrDislike(id, userId);
    }
}
