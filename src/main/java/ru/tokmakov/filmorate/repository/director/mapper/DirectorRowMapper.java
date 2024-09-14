package ru.tokmakov.filmorate.repository.director.mapper;


import org.springframework.jdbc.core.RowMapper;
import ru.filmorate.model.Director;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DirectorRowMapper implements RowMapper<Director> {
    public Director mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Director(rs.getLong("DIRECTOR_ID"), rs.getString("NAME"));
    }
}
