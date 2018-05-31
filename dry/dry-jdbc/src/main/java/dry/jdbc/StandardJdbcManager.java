package dry.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class StandardJdbcManager implements JdbcManager {

  private final static Map<Integer, Object[]> CACHE_SQL = new HashMap<>();

  private final static int CACHE_INDEX_SQL = 0;

  private final static int CACHE_INDEX_BINDS = 1;

  private final static String[] SEARCH = new String[] { " ", ",", ")", "(" };

  private final Connection connection;

  protected StandardJdbcManager(Connection connection) {
	this.connection = connection;
  }

  private Connection getConnection() {
	return connection;
  }

  @Override
  public int executeUpdate(String sql) throws SQLException {
	return processUpdate(sql, Collections.emptyList());
  }

  public int executeUpdate(String sql, Collection<Object> values) throws SQLException {
	return processUpdate(sql, values);
  }

  @Override
  public int executeUpdate(String sql, Map<String, Object> values) throws SQLException {
	return processUpdate(sql, values);
  }

  @Override
  public <T> T executeQuery(String sql, JdbcResult<T> result) throws SQLException {
	return processQuery(sql, result, Collections.emptyList());
  }

  @Override
  public <T> T executeQuery(String sql, JdbcResult<T> result, Collection<Object> values) throws SQLException {
	return processQuery(sql, result, values);
  }

  @Override
  public <T> T executeQuery(String sql, JdbcResult<T> result, Map<String, Object> values) throws SQLException {
	return processQuery(sql, result, values);
  }

  private <T> T processQuery(String sql, JdbcResult<T> result, Collection<Object> values) throws SQLException {
	PreparedStatement stmt = null;
	ResultSet rs = null;
	try {
	  stmt = prepare(getConnection(), sql, values);
	  rs = stmt.executeQuery();
	  T instance = null;
	  if (rs.next()) {
		instance = result.newInstance();
		if (result.setValue(instance, rs)) {
		  while (rs.next()) {
			if (!result.setValue(instance, rs)) {
			  break;
			}
		  }
		}
	  }
	  return instance;
	} finally {
	  close(stmt, rs);
	}
  }

  private <T> T processQuery(String sql, JdbcResult<T> result, Map<String, Object> values) throws SQLException {
	PreparedStatement stmt = null;
	ResultSet rs = null;
	try {
	  stmt = prepare(getConnection(), sql, values);
	  rs = stmt.executeQuery();
	  T instance = null;
	  if (rs.next()) {
		instance = result.newInstance();
		if (result.setValue(instance, rs)) {
		  while (rs.next()) {
			if (!result.setValue(instance, rs)) {
			  break;
			}
		  }
		}
	  }
	  return instance;
	} finally {
	  close(stmt, rs);
	}
  }

  @Override
  public void close() throws SQLException {
	if (!getConnection().isClosed()) {
	  close(getConnection());
	}
  }

  private void close(AutoCloseable... closeable) {
	for (int i = 0; i < closeable.length; i++) {
	  try {
		closeable[i].close();
	  } catch (Exception ignore) {
	  }
	}
  }

  private int processUpdate(String sql, Collection<Object> values) throws SQLException {
	PreparedStatement statement = null;
	try {
	  statement = prepare(getConnection(), sql, values);
	  return statement.executeUpdate();
	} finally {
	  close(statement);
	}
  }

  private int processUpdate(String sql, Map<String, Object> values) throws SQLException {
	PreparedStatement statement = null;
	try {
	  statement = prepare(getConnection(), sql, values);
	  return statement.executeUpdate();
	} finally {
	  close(statement);
	}
  }

  private PreparedStatement prepare(Connection connection, String sql, Collection<Object> values) throws SQLException {
	PreparedStatement statement = connection.prepareStatement(sql);
	prepareParameter(statement, values);
	return statement;
  }

  private PreparedStatement prepare(Connection connection, String sql, Map<String, Object> values) throws SQLException {
	String prepare = sql.trim();
	StringBuilder sqlBuilder = new StringBuilder(prepare);
	Collection<Object> parameters = Collections.emptyList();
	int index = 0;
	int start = 0;
	if ((index = sqlBuilder.indexOf(":", start)) != -1) {
	  String name;
	  int hashCode = prepare.hashCode();
	  Object[] cache = getCache(hashCode);
	  parameters = new ArrayList<>();
	  if (cache == null) {
		List<String> binds = new ArrayList<>();
		do {
		  index = searchIndexOf(sqlBuilder, (start = index), SEARCH);
		  if (index != start) {
			name = sqlBuilder.substring(start + 1, index);
			prepareParameter(name, values, parameters);
			sqlBuilder.replace(start, index, "?");
			binds.add(name);
		  }
		} while ((index = sqlBuilder.indexOf(":", start)) != -1);
		prepare = sqlBuilder.toString();
		addCache(hashCode, prepare, binds.toArray(new String[binds.size()]));
	  } else {
		prepare = getSql(cache);
		String[] binds = getBinds(cache);
		for (int i = 0; i < binds.length; i++) {
		  prepareParameter(binds[i], values, parameters);
		}
	  }
	}
	return prepare(connection, prepare, parameters);
  }

  private Object[] getCache(int hashCode) {
	return CACHE_SQL.get(hashCode);
  }

  private String getSql(Object[] cache) {
	return (String) cache[CACHE_INDEX_SQL];
  }

  private String[] getBinds(Object[] cache) {
	return (String[]) cache[CACHE_INDEX_BINDS];
  }

  private void addCache(int hashCode, String sql, String[] binds) {
	CACHE_SQL.put(hashCode, new Object[] { sql, binds });
  }

  private void prepareParameter(String name, Map<String, Object> values, Collection<Object> parameters) {
	if (values.containsKey(name)) {
	  parameters.add(values.get(name));
	}
  }

  private int searchIndexOf(StringBuilder sqlBuilder, int start, String[] search) {
	int index;
	int result = sqlBuilder.length();
	for (int i = 0; i < search.length; i++) {
	  if ((index = sqlBuilder.indexOf(search[i], start)) != -1) {
		result = Math.min(result, index);
	  }
	}
	return result;
  }

  private void prepareParameter(PreparedStatement statement, Collection<Object> values) throws SQLException {
	int i = 0;
	for (Object value : values) {
	  setParameter(statement, ++i, value);
	}
  }

  private void setParameter(PreparedStatement statement, int index, Object value) throws SQLException {
	statement.setObject(index, value);
  }
}