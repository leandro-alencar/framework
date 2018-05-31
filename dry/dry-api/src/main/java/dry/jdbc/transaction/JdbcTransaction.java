package dry.jdbc.transaction;

import java.sql.Connection;

public interface JdbcTransaction {

  public void begin();

  public void commit();

  public void rollback();

  public Connection getConnection();
}