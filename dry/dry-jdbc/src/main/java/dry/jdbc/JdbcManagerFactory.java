package dry.jdbc;

import java.sql.Connection;

public final class JdbcManagerFactory {

  private JdbcManagerFactory() {
	super();
  }

  public static JdbcManager create(Connection connection) {
	return new StandardJdbcManager(connection);
  }
}