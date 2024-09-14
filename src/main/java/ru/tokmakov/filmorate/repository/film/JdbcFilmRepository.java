package ru.tokmakov.filmorate.repository.film;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.filmorate.exception.NotFoundException;
import ru.filmorate.exception.ValidationException;
import ru.filmorate.model.Director;
import ru.filmorate.model.Film;
import ru.filmorate.model.Genre;
import ru.filmorate.repository.film.mapper.FilmRowMapper;
import ru.filmorate.repository.film.mapper.FilmsExtractor;
import ru.filmorate.repository.genre.mapper.GenreRowMapper;

import java.sql.Date;
import java.util.*;

@Repository
@RequiredArgsConstructor
public class JdbcFilmRepository implements FilmRepository {

    private static final Logger log = LoggerFactory.getLogger(JdbcFilmRepository.class);
    private final NamedParameterJdbcOperations jdbs;


    @Override
    public Optional<Film> getById(long filmId) {
        String sql = "SELECT * FROM films AS f " +
                "LEFT OUTER JOIN MPA_rating AS mr ON f.mpa_rating_id = mr.mpa_rating_id " +
                "WHERE f.film_id = :filmId";
        try {
            Film film = jdbs.queryForObject(sql, Map.of("filmId", filmId), new FilmRowMapper());
            if (film.getGenres() != null) {
                LinkedHashSet<Genre> genres = getGenres(filmId);
                film.setGenres(genres);
            }
            return Optional.ofNullable(film);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Фильм с id " + filmId + " не найден");
        }

    }

    @Override
    public Film save(Film film) {
        GeneratedKeyHolder keyHolderFilms = new GeneratedKeyHolder();


        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("name", film.getName())
                .addValue("description", film.getDescription())
                .addValue("release_date", Date.valueOf(film.getReleaseDate()))
                .addValue("duration_in_sec", film.getDuration())
                .addValue("mpa_rating_id", film.getMpa().getId());


        jdbs.update("INSERT INTO FILMS (" +
                        "NAME, DESCRIPTION, RELEASE_DATE, DURATION_IN_MIN, MPA_RATING_ID) " +
                        "VALUES (:name, :description, :release_date, :duration_in_sec, :mpa_rating_id)",
                params, keyHolderFilms, new String[]{"film_id"});


        film.setId(keyHolderFilms.getKeyAs(Long.class));

        createFilmGenresBond(film);
        createFilmDirectorsBond(film);

        return getById(film.getId()).orElseThrow();


    }

    @Override
    public Film update(Film film) {

        if (getById(film.getId()).isPresent()) {

            String sqlUpdateFilm = "UPDATE FILMS SET " +
                    "NAME = :name, " +
                    "DESCRIPTION = :description, " +
                    "RELEASE_DATE = :releaseDate, " +
                    "DURATION_IN_MIN = :duration, " +
                    "MPA_RATING_ID = :mpa_rating_id " +
                    "WHERE FILM_ID = :filmId";

            jdbs.update(sqlUpdateFilm, Map.of(
                    "filmId", film.getId(),
                    "name", film.getName(),
                    "description", film.getDescription(),
                    "releaseDate", film.getReleaseDate(),
                    "duration", film.getDuration(),
                    "mpa_rating_id", film.getMpa().getId()));

            String sqlDeleteGenresBond = "DELETE FROM FILM_GENRE WHERE FILM_ID = :filmId";
            jdbs.update(sqlDeleteGenresBond, Map.of("filmId", film.getId()));

            createFilmGenresBond(film);
            createFilmDirectorsBond(film);

            return film;

        } else {
            log.warn("Фильм для обновления с id {} не найден", film.getId());
            throw new NotFoundException("Фильм для обновления с id " + film.getId() + " не найден");
        }

    }

    @Override
    public void deleteById(long filmId) {
        String query = "DELETE FROM FILMS WHERE FILM_ID = :filmId";
        jdbs.update(query, Map.of("filmId", filmId));
    }

    @Override
    public Collection<Film> getAll() {
        String sql = "Select * FROM films AS f " +
                "LEFT JOIN film_genre AS fg ON fg.film_id = f.film_id " +
                "LEFT JOIN genres AS g ON g.genre_id = fg.genre_id " +
                "LEFT JOIN MPA_rating AS mr ON f.mpa_rating_id = mr.mpa_rating_id";

        return setGenresAndDirectors(jdbs.query(sql, new FilmsExtractor()));
    }


    @Override
    public Collection<Film> getTopPopular(Long countTop, Integer genreId, Integer year) {

        Collection<Film> topFilms;
        Map<String, Object> params = new HashMap<>();

        String sqlTopFilmsCount = "";
        String sqlTopFilmsGenreId = "";
        String sqlTopFilmsYear = "";
        String sqlWhere = "";
        String sqlAnd = "";

        if (countTop != null) {
            log.debug("Выполнено условие лимит не равен null");
            sqlTopFilmsCount = "LIMIT :countTop";
            params.put("countTop", countTop);
        }
        if (genreId != null) {
            log.debug("Выполнено условие год жанр не равен null");
            sqlWhere = "WHERE ";
            sqlTopFilmsGenreId = "fg.genre_id = :genreId ";
            params.put("genreId", genreId);
        }
        if (year != null) {
            log.debug("Выполнено условие год не равен null");
            sqlWhere = "WHERE ";
            sqlTopFilmsYear = "YEAR(f.release_date) = :year ";
            params.put("year", year);
        }

        if (genreId != null && year != null) {
            log.debug("Выполнено условие жанр и год не равны null");
            sqlAnd = "AND ";
        }


        String sqlTopFilmsBase = "SELECT f.*, mr.name " +
                "FROM FILMS AS f " +
                "LEFT JOIN LIKES AS l ON f.film_id = l.film_id " +
                "LEFT JOIN MPA_RATING AS mr ON f.mpa_rating_id = mr.mpa_rating_id " +
                "LEFT JOIN FILM_GENRE AS fg ON f.film_id = fg.film_id " +
                sqlWhere + sqlTopFilmsGenreId + sqlAnd + sqlTopFilmsYear +
                "GROUP BY f.film_id " +
                "ORDER BY COUNT(l.film_id) DESC " +
                sqlTopFilmsCount;


        topFilms = jdbs.query(sqlTopFilmsBase, params,
                new FilmsExtractor());

        return setGenresAndDirectors(topFilms);

    }

    @Override
    public Collection<Film> getSortedFilmsByDirector(long directorId, String sortBy) {

        String queryYear = "SELECT f.*, d.DIRECTOR_ID, mr.NAME " +
                "FROM FILM_DIRECTOR AS fd " +
                "LEFT JOIN FILMS AS f ON fd.FILM_ID = f.FILM_ID " +
                "LEFT JOIN MPA_RATING AS mr ON f.MPA_RATING_ID = mr.MPA_RATING_ID " +
                "LEFT JOIN DIRECTORS AS d ON fd.DIRECTOR_ID = d.DIRECTOR_ID " +
                "WHERE fd.DIRECTOR_ID = :directorId " +
                "ORDER BY year(f.RELEASE_DATE)";

        String queryLikes = "SELECT f.*, " +
                "d.DIRECTOR_ID, " +
                "mr.NAME, " +
                "(SELECT count(*) FROM LIKES AS l WHERE fd.FILM_ID = l.FILM_ID) AS LIKESC " +
                "FROM FILM_DIRECTOR AS fd " +
                "LEFT JOIN FILMS AS f ON fd.FILM_ID = f.FILM_ID " +
                "LEFT JOIN MPA_RATING AS mr ON f.MPA_RATING_ID = mr.MPA_RATING_ID " +
                "LEFT JOIN DIRECTORS AS d ON fd.DIRECTOR_ID = d.DIRECTOR_ID " +
                "WHERE fd.DIRECTOR_ID = :directorId " +
                "ORDER BY LIKESC DESC ";

        switch (sortBy) {
            case "year":
                log.info("Вариант сортировки по году:  " + sortBy);
                Collection<Film> directorFilmsByYear = jdbs.query(queryYear, Map.of("directorId", directorId),
                        new FilmsExtractor());
                log.info("Из DB получен список размером:  " + directorFilmsByYear.size());

                return setGenresAndDirectors(directorFilmsByYear);

            case "likes":
                log.info("Вариант сортировки по лайкам:  " + sortBy);
                Collection<Film> directorFilmsByLikes = jdbs.query(queryLikes, Map.of("directorId", directorId),
                        new FilmsExtractor());
                log.info("Из DB получен список размером:  " + directorFilmsByLikes.size());

                return setGenresAndDirectors(directorFilmsByLikes);

            default:
                log.info("вариант сортировки default:  " + sortBy);
                throw new ValidationException("Неверный параметр запроса: " + sortBy);
        }
    }


    @Override
    public LinkedHashSet<Genre> getGenres(long filmId) {
        String sql = "Select fg.genre_id, g.genre_name " +
                "FROM film_genre AS fg " +
                "LEFT OUTER JOIN genres AS g ON fg.genre_id = g.genre_id " +
                "WHERE fg.film_id = :filmId " +
                "ORDER BY g.genre_id";
        return new LinkedHashSet<>(jdbs.query(sql, Map.of("filmId", filmId), new GenreRowMapper()));
    }

    private void createFilmGenresBond(Film film) {
        String sqlDel = "DELETE FROM FILM_GENRE WHERE FILM_ID = :filmId";
        jdbs.update(sqlDel, Map.of("filmId", film.getId()));

        if (film.getGenres() != null) {

            String sql = "Select genre_id from genres";
            List<Integer> genresFromDb = jdbs.queryForList(sql, new HashMap<>(), Integer.class);
            for (Genre genre : film.getGenres()) {
                if (!genresFromDb.contains(genre.getId())) {
                    throw new EmptyResultDataAccessException(genre.getId());
                }
            }

            GeneratedKeyHolder keyHolderGenres = new GeneratedKeyHolder();

            final List<Genre> genres = new ArrayList<>(film.getGenres());
            SqlParameterSource[] paramsGenres = new MapSqlParameterSource[genres.size()];
            for (int i = 0; i < genres.size(); i++) {
                paramsGenres[i] = new MapSqlParameterSource()
                        .addValue("filmId", film.getId())
                        .addValue("genreId", genres.get(i).getId());
            }

            jdbs.batchUpdate("INSERT INTO FILM_GENRE (FILM_ID, GENRE_ID) " +
                            "VALUES (:filmId, :genreId)",
                    paramsGenres, keyHolderGenres, new String[]{"film_genre_id"});
        }
    }

    private void createFilmDirectorsBond(Film film) {
        String sqlDel = "DELETE FROM FILM_DIRECTOR WHERE FILM_ID = :filmId";
        jdbs.update(sqlDel, Map.of("filmId", film.getId()));

        if (film.getDirectors() != null) {
            String sql = "Select director_id from directors";
            List<Long> directorsFromDb = jdbs.queryForList(sql, new HashMap<>(), Long.class);
            for (Director director : film.getDirectors()) {
                if (!directorsFromDb.contains(director.getId())) {
                    throw new EmptyResultDataAccessException((int) director.getId());
                }
            }

            final List<Director> directors = new ArrayList<>(film.getDirectors());
            SqlParameterSource[] paramsDirectors = new MapSqlParameterSource[directors.size()];
            for (int i = 0; i < directors.size(); i++) {
                paramsDirectors[i] = new MapSqlParameterSource()
                        .addValue("filmId", film.getId())
                        .addValue("directorId", directors.get(i).getId());
            }
            jdbs.batchUpdate("INSERT INTO FILM_DIRECTOR (FILM_ID, DIRECTOR_ID) " +
                            "VALUES (:filmId, :directorId)",
                    paramsDirectors);
        }
    }


    private Collection<Film> setGenresAndDirectors(Collection<Film> films) {
        List<Long> filmsIds = films.stream().map(Film::getId).toList();
        Map<Long, LinkedHashSet<Genre>> genres = getGenresForFilms(filmsIds);
        Map<Long, LinkedHashSet<Director>> directors = getDirectorsForFilms(filmsIds);
        for (Film film : films) {
            film.setGenres(genres.getOrDefault(film.getId(), new LinkedHashSet<>()));
            film.setDirectors(directors.getOrDefault(film.getId(), new LinkedHashSet<>()));
        }
        return films;
    }

    private Map<Long, LinkedHashSet<Genre>> getGenresForFilms(List<Long> filmIds) {
        String sql = "SELECT fg.FILM_ID, g.Genre_ID, g.GENRE_NAME " +
                "FROM FILM_GENRE AS fg " +
                "JOIN GENRES AS g ON fg.GENRE_ID = g.GENRE_ID " +
                "WHERE fg.FILM_ID IN (:filmIds)";
        if (filmIds.isEmpty()) {
            return Collections.emptyMap();
        }

        List<Map<String, Object>> rows = jdbs.queryForList(sql, Map.of("filmIds", filmIds));
        Map<Long, LinkedHashSet<Genre>> genresMap = new HashMap<>();

        for (Map<String, Object> row : rows) {
            Long filmId = (Long) row.get("FILM_ID");
            Genre genre = new Genre((Integer) row.get("GENRE_ID"), (String) row.get("GENRE_NAME"));

            genresMap.computeIfAbsent(filmId, k -> new LinkedHashSet<>()).add(genre);
        }

        return genresMap;
    }

    private Map<Long, LinkedHashSet<Director>> getDirectorsForFilms(List<Long> filmIds) {
        String sqlDirectors = "SELECT fd.FILM_ID, d.DIRECTOR_ID, d.NAME " +
                "FROM FILM_DIRECTOR AS fd " +
                "JOIN DIRECTORS AS d ON fd.DIRECTOR_ID = d.DIRECTOR_ID " +
                "WHERE fd.FILM_ID IN (:filmIds)";
        if (filmIds.isEmpty()) {
            return Collections.emptyMap();
        }

        List<Map<String, Object>> rows = jdbs.queryForList(sqlDirectors, Map.of("filmIds", filmIds));
        Map<Long, LinkedHashSet<Director>> directorsMap = new HashMap<>();

        for (Map<String, Object> row : rows) {
            Long filmId = (Long) row.get("FILM_ID");
            Director director = new Director((Long) row.get("DIRECTOR_ID"), (String) row.get("NAME"));

            directorsMap.computeIfAbsent(filmId, k -> new LinkedHashSet<>()).add(director);
        }
        return directorsMap;
    }

    @Override
    public Collection<Film> searchFilms(String query, List<String> criteria) {
        boolean searchByDirector = false;
        boolean searchByTitle = false;
        if (criteria != null) {
            for (String condition : criteria) {
                if (condition.toLowerCase(Locale.ROOT).equals("director")) {
                    searchByDirector = true;
                }
                if (condition.toLowerCase(Locale.ROOT).equals("title")) {
                    searchByTitle = true;
                }
            }
        }
        String sqlQuery = makeSearchSqlQuery(query, searchByDirector, searchByTitle);

        return setGenresAndDirectors(jdbs.query(sqlQuery, new FilmsExtractor()));
    }

    private static String makeSearchSqlQuery(String query, boolean searchByDirector, boolean searchByTitle) {
        String queryClause = "'%" + query + "%' ";
        String sqlWhereClause = " ";
        if ((query != null) && (!query.isEmpty()) && (searchByDirector || searchByTitle)) {
            String sqlWhereClauseDirector = "";
            String sqlWhereClauseTitle = "";
            String sqlOr = "";
            if (searchByDirector) {
                sqlWhereClauseDirector = "D.NAME ILIKE " + queryClause;
            }
            if (searchByTitle) {
                sqlWhereClauseTitle = "F.NAME ILIKE " + queryClause;
            }
            if (searchByDirector && searchByTitle) {
                sqlOr = "OR ";
            }
            sqlWhereClause = " WHERE " + sqlWhereClauseDirector + sqlOr + sqlWhereClauseTitle;
        }

        String sqlQuery =
                """
                        SELECT F.*, MR.NAME
                        FROM FILMS AS F
                        LEFT JOIN LIKES AS L ON F.FILM_ID = L.FILM_ID
                        LEFT JOIN MPA_RATING AS mr ON f.mpa_rating_id = mr.mpa_rating_id
                        LEFT JOIN FILM_DIRECTOR FD on f.FILM_ID = FD.FILM_ID
                        LEFT JOIN DIRECTORS D on D.DIRECTOR_ID = FD.DIRECTOR_ID
                        """ +
                        sqlWhereClause +
                        """
                                GROUP BY F.FILM_ID
                                ORDER BY COUNT(l.film_id) DESC
                                """;
        return sqlQuery;
    }
}

