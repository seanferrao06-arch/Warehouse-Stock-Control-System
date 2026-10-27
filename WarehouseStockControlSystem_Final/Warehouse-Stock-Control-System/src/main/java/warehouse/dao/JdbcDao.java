package warehouse.dao;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import warehouse.util.DatabaseException;

import java.sql.PreparedStatement;
import java.sql.Statement;

/** Small shared base for the JDBC repositories; it translates database details into safe messages. */
abstract class JdbcDao {
    protected final JdbcTemplate jdbc;

    protected JdbcDao(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    protected int insertAndGetId(String sql, Object... values) {
        KeyHolder keys = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            for (int i = 0; i < values.length; i++) statement.setObject(i + 1, values[i]);
            return statement;
        }, keys);
        Number key = keys.getKey();
        if (key == null) throw new DatabaseException("The record was saved, but its new ID could not be read.");
        return key.intValue();
    }

    protected DatabaseException databaseError(String message, DataAccessException cause) {
        return new DatabaseException(message, cause);
    }
}
