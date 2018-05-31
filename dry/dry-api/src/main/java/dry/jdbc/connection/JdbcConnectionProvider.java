package dry.jdbc.connection;

import java.sql.Connection;
import java.sql.SQLException;

public interface JdbcConnectionProvider {

  public Connection createConnenction() throws SQLException;

  public void closeConnection(Connection connection) throws SQLException;
}