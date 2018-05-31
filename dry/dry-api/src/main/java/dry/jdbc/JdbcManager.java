package dry.jdbc;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Map;

public interface JdbcManager extends AutoCloseable {

  public int executeUpdate(String sql) throws SQLException;

  public int executeUpdate(String sql, Collection<Object> values) throws SQLException;

  public int executeUpdate(String sql, Map<String, Object> values) throws SQLException;

  public <T> T executeQuery(String sql, JdbcResult<T> result) throws SQLException;

  public <T> T executeQuery(String sql, JdbcResult<T> result, Collection<Object> values) throws SQLException;

  public <T> T executeQuery(String sql, JdbcResult<T> result, Map<String, Object> values) throws SQLException;
}