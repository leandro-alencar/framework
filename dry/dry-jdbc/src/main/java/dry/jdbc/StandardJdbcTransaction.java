package dry.jdbc;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.concurrent.atomic.AtomicBoolean;

import dry.jdbc.connection.JdbcConnectionProvider;
import dry.jdbc.transaction.JdbcTransaction;

public final class StandardJdbcTransaction implements JdbcTransaction {

  private JdbcConnectionProvider provider;

  private JdbcTransaction parent;

  private Connection connection;

  private Savepoint savepoint;

  private AtomicBoolean autoCommit;

  private AtomicBoolean activeTransaction;

  private StandardJdbcTransaction(JdbcConnectionProvider provider, JdbcTransaction parent) {
	super();
	this.provider = provider;
	this.parent = parent;
	this.autoCommit = new AtomicBoolean(false);
	this.activeTransaction = new AtomicBoolean(false);
  }

  public StandardJdbcTransaction(JdbcConnectionProvider provider) {
	this(provider, null);
  }

  public StandardJdbcTransaction(JdbcTransaction parent) {
	this(null, parent);
	this.parent = parent;
  }

  @Override
  public void begin() {
	if (!activeTransaction.get()) {
	  synchronized (activeTransaction) {
		if (!activeTransaction.get()) {
		  if (this.provider != null) {
			begin(provider);
		  } else {
			begin(parent);
		  }
		  activeTransaction.set(true);
		} else {
		  // TODO Exception transaction active
		}
	  }
	} else {
	  // TODO Exception transaction active
	}
  }

  private void begin(JdbcConnectionProvider provider) {
	try {
	  connection = provider.createConnenction();
	  autoCommit.set(connection.getAutoCommit());
	  if (autoCommit.get()) {
		connection.setAutoCommit(false);
	  }
	} catch (SQLException e) {
	  e.printStackTrace();
	}
  }

  private void begin(JdbcTransaction parent) {

  }

  @Override
  public void commit() {
	try {
	  if (this.savepoint != null) {
		getConnection().releaseSavepoint(this.savepoint);
	  }
	} catch (SQLException e) {
	  e.printStackTrace();
	}
  }

  @Override
  public void rollback() {
	// TODO Auto-generated method stub
  }

  @Override
  public Connection getConnection() {
	return this.connection;
  }
}