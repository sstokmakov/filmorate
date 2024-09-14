package ru.tokmakov.filmorate.repository.mpa;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import ru.filmorate.exception.NotFoundException;
import ru.filmorate.model.MPARating;
import ru.filmorate.repository.mpa.mapper.MPARowMapper;

import java.util.*;

@Repository
@RequiredArgsConstructor
public class JdbcMPARatingRepository implements MPARatingRepository {
    private final NamedParameterJdbcOperations jdbc;

    @Override
    public Collection<MPARating> getAllMPARatings() {
        String sql = "select * from mpa_rating";
        List<MPARating> mpaRatingList = jdbc.query(sql, new MPARowMapper());
        HashMap<Integer, MPARating> mpaRatingMap = new HashMap<>();
        for (MPARating mpaRating : mpaRatingList) {
            mpaRatingMap.put(mpaRating.getId(), mpaRating);
        }

        return new LinkedHashSet<>(mpaRatingMap.values());

    }

    @Override
    public MPARating getMPARatingById(int mpaId) {
        try {
            String sql = "SELECT * FROM mpa_rating WHERE mpa_rating_id = :mpaId";

            return jdbc.queryForObject(sql, Map.of("mpaId", mpaId), new MPARowMapper());
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("No MPARating with id " + mpaId);
        }
    }


}
