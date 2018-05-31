package dry.jdbc;

import dry.jdbc.connection.JdbcConnectionProvider;
import dry.jdbc.transaction.JdbcTransaction;

public class JdbcTransactionTest {

  public static void main(String[] args) throws Exception {
	String className = "com.mysql.jdbc.Driver";
	String url = "jdbc:mysql://localhost:3306/jdbc_manager";
	String user = "root";
	String password = "admin@mysql";
	JdbcConnectionProvider connectionProvider = new DriverJdbcConnectionProvider(className, url, user, password);
	JdbcTransaction transaction = new StandardJdbcTransaction(connectionProvider);
	try {
	  transaction.begin();
	  System.out.println(transaction.getConnection());
	  transaction.commit();
	} catch (Exception e) {
	  transaction.rollback();
	}
  }
}