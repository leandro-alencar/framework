package dry.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface JdbcResult<T> {

  public T newInstance();

  public boolean setValue(T instance, ResultSet resultSet) throws SQLException;
}