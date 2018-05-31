package dry.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JdbcManagerTest {

  public static void main(String[] args) throws Exception {
	Class.forName("com.mysql.jdbc.Driver").newInstance();
	String url = "jdbc:mysql://localhost:3306/jdbc_manager";
	String user = "root";
	String pass = "admin@mysql";
	Connection connection = DriverManager.getConnection(url, user, pass);
	connection.setAutoCommit(false);
	long inicio = System.currentTimeMillis();
	try (JdbcManager manager = JdbcManagerFactory.create(connection)) {
	  for (int i = 0; i < 100; i++) {
		Collection<Object> values = new ArrayList<>();
		values.add("Leandro dos Santos");
		values.add("leandro.alencar");
		values.add(System.currentTimeMillis());
		values.add(new Date());

		Map<String, Object> named = new HashMap<>();
		named.put("nome", "Leandro dos Santos");
		named.put("login", "leandro.alencar");
		named.put("senha", System.currentTimeMillis());
		named.put("dt_cadastro", new Date());
		named.put("id", 2);

		update(manager, "insert into usuario(nome, login, senha, dt_cadastro) values (?, ?, ?, ?)", values);
		update(manager,
			"insert into usuario(nome, login, senha, dt_cadastro) values (:nome, :login, :senha, :dt_cadastro)", named);

		query(manager, "select nome, login, senha from usuario where (nome = ?) and id <= ?",
			Arrays.<Object>asList("Leandro dos Santos", 2));
		query(manager, "select nome, login, senha from usuario where ((nome = :nome) or (nome = :nome)) and id <= :id",
			named);
	  }
	  connection.commit();
	} catch (Exception e) {
	  connection.rollback();
	  e.printStackTrace();
	}
	System.out.println(System.currentTimeMillis() - inicio);
  }

  private static void update(JdbcManager manager, String sql, Collection<Object> values) throws Exception {
	int update = manager.executeUpdate(sql, values);
	System.out.println(update);
  }

  private static void update(JdbcManager manager, String sql, Map<String, Object> values) throws Exception {
	int update = manager.executeUpdate(sql, values);
	System.out.println(update);
  }

  private static void query(JdbcManager manager, String sql, Map<String, Object> values) throws Exception {
	Map<String, String> result = manager.executeQuery(sql, new JdbcResult<Map<String, String>>() {
	  @Override
	  public Map<String, String> newInstance() {
		return new HashMap<>();
	  }

	  @Override
	  public boolean setValue(Map<String, String> instance, ResultSet resultSet) throws SQLException {
		instance.put("nome", resultSet.getString("nome"));
		instance.put("login", resultSet.getString("login"));
		instance.put("senha", resultSet.getString("senha"));
		return false;
	  }
	}, values);
	System.out.println(result);
  }

  private static void query(JdbcManager manager, String sql, Collection<Object> values) throws Exception {
	Map<String, String> result = manager.executeQuery(sql, new JdbcResult<Map<String, String>>() {
	  @Override
	  public Map<String, String> newInstance() {
		return new HashMap<>();
	  }

	  @Override
	  public boolean setValue(Map<String, String> instance, ResultSet resultSet) throws SQLException {
		instance.put("nome", resultSet.getString("nome"));
		instance.put("login", resultSet.getString("login"));
		instance.put("senha", resultSet.getString("senha"));
		return false;
	  }
	}, values);
	System.out.println(result);
  }
}