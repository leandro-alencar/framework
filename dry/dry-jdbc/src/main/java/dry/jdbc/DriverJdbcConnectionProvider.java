package dry.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import dry.jdbc.connection.JdbcConnectionProvider;

public class DriverJdbcConnectionProvider implements JdbcConnectionProvider {

  private String url;

  private String user;

  private String password;

  public DriverJdbcConnectionProvider(String className, String url, String user, String password) {
	try {
	  Class.forName(className, true, Thread.currentThread().getContextClassLoader());
	} catch (ClassNotFoundException e) {
	  throw new RuntimeException(e);
	}
	this.url = url;
	this.user = user;
	this.password = password;
  }

  @Override
  public Connection createConnenction() throws SQLException {
	return DriverManager.getConnection(this.url, this.user, this.password);
  }

  @Override
  public void closeConnection(Connection connection) throws SQLException {
	if (!connection.isClosed()) {
	  connection.close();
	}
  }
}