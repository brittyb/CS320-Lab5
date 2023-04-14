package edu.ycp.cs320.booksdb;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;

import edu.ycp.cs320.sqldemo.DBUtil;

public class InsertNewBookWithAuthor {
	public static void main(String[] args) throws Exception {
		// load Derby JDBC driver
		try {
			Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
		} catch (Exception e) {
			System.err.println("Could not load Derby JDBC driver");
			System.err.println(e.getMessage());
			System.exit(1);
		}

		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet resultSet = null;
		String id = "";

		// connect to the database
		conn = DriverManager.getConnection("jdbc:derby:test.db;create=true");

		@SuppressWarnings("resource")
		Scanner keyboard = new Scanner(System.in);

		try {
			conn.setAutoCommit(true);
			
			// prompt user for author name
			System.out.print("Author's first name: ");
			String firstname = keyboard.nextLine();
			System.out.print("Author's last name: ");
			String lastname = keyboard.nextLine();
			
			// a canned query to find author id based on author's name
			stmt = conn.prepareStatement(
					"select authors.author_id "
					+ "  from authors"
					+ "  where authors.lastname = ?"
					+"		   and authors.firstname = ?"
			);
			
			stmt.setString(1, lastname);
			stmt.setString(2, firstname);
			// execute the query
			resultSet = stmt.executeQuery();
			
			int rowsReturned = 0;
			if(resultSet.next()) {
				id = resultSet.getString(1);
			}
			while(resultSet.next()) {
				rowsReturned++;
			}
			if(rowsReturned == 0) {
				DBUtil.closeQuietly(resultSet);
				DBUtil.closeQuietly(stmt);
				stmt = conn.prepareStatement(
						"insert into authors(lastname, firstname) "
						+ "  values(?, ?)"
				);
				stmt.setString(1, lastname);
				stmt.setString(2, firstname);
				stmt.executeUpdate();
				
				DBUtil.closeQuietly(resultSet);
				DBUtil.closeQuietly(stmt);
				stmt = conn.prepareStatement(
						"select authors.author_id "
						+ "  from authors"
						+ "  where authors.lastname = ?"
						+"		   and authors.firstname = ?"
				);
				
				stmt.setString(1, lastname);
				stmt.setString(2, firstname);
				resultSet = stmt.executeQuery();
				if(resultSet.next()) {
					id = resultSet.getString(1);
				}
			
				
			}


			
			//get inputs for title, isbn, and published
			System.out.print("Book title: ");
			String title = keyboard.nextLine();
			System.out.print("Book ISBN: ");
			String isbn = keyboard.nextLine();
			System.out.print("Year published: ");
			String published = keyboard.nextLine();

			DBUtil.closeQuietly(resultSet);
			DBUtil.closeQuietly(stmt);
			
			//check if book exists
			stmt = conn.prepareStatement(
					"select books.title "
					+ "  from books"
					+ "  where books.title = ?"
					+ "  and books.isbn = ?"
					+ "  and books.published = ? and books.author_id = ?"
					);
			
			stmt.setString(1, title);
			stmt.setString(2, isbn);
			stmt.setString(3, published);
			stmt.setString(4, id);
			resultSet = stmt.executeQuery();
			rowsReturned = 0;
			while(resultSet.next()) {
				rowsReturned++;
			}
			if(rowsReturned != 0) {
				System.out.print("Book already exists");
			}else {
				DBUtil.closeQuietly(resultSet);
				DBUtil.closeQuietly(stmt);
				// insert book
				stmt = conn.prepareStatement(
						"insert into books(author_id, title, isbn, published) "
						+ "  values(?, ?, ?, ?)"
				);
				stmt.setString(1, id);
				stmt.setString(2, title);
				stmt.setString(3, isbn);
				stmt.setString(4, published);

				stmt.executeUpdate();
			}
			
			
			
		} finally {
			// close result set, statement, connection
			DBUtil.closeQuietly(resultSet);
			DBUtil.closeQuietly(stmt);
			DBUtil.closeQuietly(conn);
		}
		
		// connect to the database
				conn = DriverManager.getConnection("jdbc:derby:test.db;create=true");
		
	
	}
	
}

