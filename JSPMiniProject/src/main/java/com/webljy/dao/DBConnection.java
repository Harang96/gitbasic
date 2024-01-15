package com.webljy.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class DBConnection {
	private static DBConnection instance;
	
	private DBConnection() {}

	public static DBConnection getInstance() {
		if (instance == null) {
			instance = new DBConnection();
		}
		
		return instance;
	}
	
	public Connection connectDB() throws NamingException, SQLException {
		Context initContext = new InitialContext();
		Context envContext  = (Context)initContext.lookup("java:/comp/env");
		DataSource ds = (DataSource)envContext.lookup("jdbc/mySqlMini");
		Connection conn = ds.getConnection();
		System.out.println(conn.toString());
		
		return conn;
	}
	
	public void dbClose(ResultSet rs, Statement st, Connection con) throws SQLException {
		rs.close();
		st.close();
		con.close();
	}
	
	public void dbClose(Statement st, Connection con) throws SQLException {
		st.close();
		con.close();
	}
	
	public void dbClose(Connection con) throws SQLException {
		con.close();
	}
}