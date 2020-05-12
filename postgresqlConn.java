/*This program connects to PostgreSQL database on the local host, port 5432.
It implies that the datbase, db, already exists in PostgreSQL database and uses
the local username and password to enter the database.

This program was written using the Eclipse IDE and the postgreSQL jar file was added to
the build path.

Program uses the SQL API to connect to the database to create, update, and delete
entries from the databse.

The program uses the text files: activity.txt, bmis.txt, and users.txt to upload
test entries into the database.

*/

import java.sql.*;
import java.util.Scanner;
import java.io.*;

public class postgresqlConn {

	private final String jdbcUrl = "jdbc:postgresql://localhost:5432/db"; //url to database
	private final String user = "postgres"; //username for database
	private final String pw = "admin"; //password for database

	Statement stmt = null;
	ResultSet results = null;

	// create tables in the postgresql database
	public void createtables(Connection conn) throws SQLException {
		try {
			Statement st = conn.createStatement();
			String qs = "CREATE TABLE IF NOT EXISTS USERS(username varchar(225) NOT NULL UNIQUE PRIMARY KEY,password varchar(225),First_Name varchar(225), Last_Name varchar(225))";
			st.execute(qs);

			String BMITable = "CREATE TABLE IF NOT EXISTS BMI(username varchar(255) NOT NULL, weight real NOT NULL, height real NOT NULL, BMI real)";
			st.execute(BMITable);

			String Activity = "CREATE TABLE IF NOT EXISTS ACTIVITY(username varchar(255) NOT NULL, Activity varchar(255), Time_Elapsed_Minutes integer NOT NULL, Calories_Burned integer)";
			st.execute(Activity);

			System.out.println("connected");
		} catch (SQLException e) {
			System.out.println("Could not connect");
			System.exit(1);
		}
	}

   //creates connection into the postgresql database
	public Connection connect() {
		Connection conn = null;
		try {
			Class.forName("org.postgresql.Driver"); // will load driver from jar file
			conn = DriverManager.getConnection(jdbcUrl, user, pw);

			if (conn != null) {
				System.out.println("Connection made");
				stmt = conn.createStatement();
			}

		} catch (ClassNotFoundException e) {
			System.out.println("PostgreSLQ JDBC driver not found");
			e.printStackTrace();
		} catch (SQLException e) {
			System.out.println("Connection failure");
			e.printStackTrace();
		}
		return conn;

	}

	// inserts new user
	public boolean insertuser(Connection conn, String first, String last, String username, String pw) {

		try {
			PreparedStatement st = conn.prepareStatement(
					"INSERT INTO USERS (username, password, First_Name, Last_Name) VALUES (?, ?, ?, ?)");
			st.setString(1, username);
			st.setString(2, pw);
			st.setObject(3, first);
			st.setObject(4, last);
			st.executeUpdate();
			st.close();
			System.out.println("added");

			login(conn, username, pw);

			return true;
		} catch (SQLException ex) {
			System.out.println(ex.getMessage());
			return false;
		}

	}
	//Method to add users from a file read in by scanner
	public static void insertusers(Connection conn, Scanner users) {

		try {
			PreparedStatement st = conn.prepareStatement(
					"INSERT INTO USERS (username, password, First_Name, Last_Name) VALUES (?, ?, ?, ?)");
			while(users.hasNext()) {
			st.setString(1, users.next());
			st.setString(2, users.next());
			st.setObject(3, users.next());
			st.setObject(4, users.next());
			st.executeUpdate();
			}
			st.close();
			System.out.println("added Users");

			//login(conn, username, pw);


		} catch (SQLException ex) {
			System.out.println(ex.getMessage());

		}

	}
	//method to insert Activity from file
	public static void insertActivity(Connection conn, Scanner activity) {

		try {
			PreparedStatement st = conn.prepareStatement(
					"INSERT INTO activity (username, activity, time_elapsed_minutes, calories_burned) VALUES (?, ?, ?, ?)");
			while(activity.hasNext()) {
			st.setString(1, activity.next());
			st.setString(2, activity.next());
			st.setInt(3, activity.nextInt());
			st.setInt(4, activity.nextInt());
			st.executeUpdate();
			}
			st.close();
			System.out.println("added activities");

			//login(conn, username, pw);


		} catch (SQLException ex) {
			System.out.println(ex.getMessage());

		}

	}

	public static void insertBMIs(Connection conn, Scanner BMIs) {

		try {
			PreparedStatement st = conn.prepareStatement(
					"INSERT INTO bmi (username, weight, height, bmi) VALUES (?, ?, ?, ?)");
			while(BMIs.hasNext()) {
			st.setString(1, BMIs.next());
			st.setDouble(2, BMIs.nextDouble());
			st.setDouble(3, BMIs.nextDouble());
			st.setDouble(4, BMIs.nextDouble());
			st.executeUpdate();
			}
			st.close();
			System.out.println("added BMIs");

			//login(conn, username, pw);


		} catch (SQLException ex) {
			System.out.println(ex.getMessage());

		}

	}

	/*
	 * MEthod to match password entered by user to password in the users table
	 */
	public static boolean login(Connection conn, String user, String pw) {

		try {
			String searchUser = "SELECT password, first_name FROM USERS WHERE username = '" + user + "'";

			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(searchUser);

			while (rs.next()) {
				String pass = rs.getString("password");
				if (pass.equals(pw)) {
					System.out.println("welcome " + rs.getString("first_name"));
					return true;
				} else {
					System.out.println("does not match");
					return false;
				}
			}
			return false;
			// System.out.println("added");
		} catch (SQLException ex) {
			System.out.println(ex.getMessage());
			return false;
		}

	}

	/*
	 * Method to make sure usernames are not repeated in table
	 */

	public boolean checkUsername(Connection conn, String username) {
		try {
			String check = "SELECT username FROM USERS WHERE username = '" + username + "'";
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(check);
			if (rs.next()) {
				System.out.println("username exists, try a different one");
				return true;
			} else
				return false;
		} catch (SQLException ex) {
			System.out.println(ex.getMessage());
			return false;
		}
	}

	/*
	 * method to update user table: user can update their username user can update
	 * their password and user can delete their information from the table
	 */
	public String updateUsername(Connection conn, String user, String pw, Scanner sc) {

			System.out.println("Enter new username");
			String newusername = sc.next();
			if (!checkUsername(conn, newusername)) {
				try {
					String update = "UPDATE USERS " + "SET username = ?" + "WHERE username = ?";
					PreparedStatement pst = conn.prepareStatement(update);
					pst.setString(1, newusername);
					pst.setString(2, user);
					pst.executeUpdate();
					return newusername;
				} catch (SQLException ex) {
					System.out.println(ex.getMessage());
					//return user;

				}
			}
		   return user;

	}

	public void updatePassword(Connection conn, String user, String pw, Scanner sc) {

			System.out.println("Enter new password");
			String newpw = sc.next();
			try {
				String update = "UPDATE USERS " + "SET password = ?" + "WHERE username = ?";
				PreparedStatement pst = conn.prepareStatement(update);
				pst.setString(1, newpw);
				pst.setString(2, user);
				pst.executeUpdate();
				System.out.println("Password changed");
			} catch (SQLException ex) {
				System.out.println(ex.getMessage());

			}

	}

	public void deleteUser(Connection conn, String user) {
		try {
			String dlte = "DELETE from USERS where username = ? ";

			PreparedStatement pst = conn.prepareStatement(dlte);
			pst.setString(1, user);
			pst.executeUpdate();
			System.out.println("user deleted");
		} catch (SQLException ex) {
			System.out.println(ex.getMessage());

		}

	}

	/*
	 * checkBMI allows user to log their bmi in a bmi table and will return their
	 * bmi based on weight and height entered
	 *
	 */

	public void checkBMI(Connection conn, String user, double weight, double height, Scanner sc) {
		double bmi = 703 * (weight / (height * height));
		String insert = "INSERT INTO bmi(username, weight, height, bmi)" + "VALUES(?,?,?,?)";
		try {
			PreparedStatement pst = conn.prepareStatement(insert);
			pst.setString(1, user);
			pst.setDouble(2, weight);
			pst.setDouble(3, height);
			pst.setDouble(4, bmi);
			pst.executeUpdate();
			System.out.println("BMI = " + bmi);
		} catch (SQLException ex) {
			System.out.println(ex.getMessage());
		}

	}

/*
 * Adds activity of user into activity table
 */
	public void addActivity(Connection conn, String user, String action, int type, int time) {
		int calories = 0;
		if (type == 1) {
			calories = time * 5;
		} else {
			calories = time * 3;
		}
		String insert = "INSERT INTO activity(username, activity, time_elapsed_minutes, calories_burned)"
				+ "VALUES(?,?,?,?)";

		try {
			PreparedStatement pst = conn.prepareStatement(insert);
			pst.setString(1, user);
			pst.setString(2, action);
			pst.setInt(3, time);
			pst.setInt(4, calories);
			pst.executeUpdate();
			System.out.println("calories burned = " + calories);
		} catch (SQLException ex) {
			System.out.println(ex.getMessage());
		}
	}

	/*
	 * Method that SELECTS info from specific table specified by user option
	 * using prepared statements to complete query strings
	 */
	public void getInfo(Connection conn, String user, int option) {

		if (option == 1) {
			String BMIinfo = "SELECT * FROM bmi WHERE username = ?";
			try {
				PreparedStatement pst = conn.prepareStatement(BMIinfo);
				pst.setString(1, user);
				ResultSet rs = pst.executeQuery();
				displayBMI(rs);
			} catch (SQLException ex) {
				System.out.println(ex.getMessage());
			}

		} else if (option == 2) {
			String BMIinfo = "SELECT * FROM activity WHERE username = ?";
			try {
				PreparedStatement pst = conn.prepareStatement(BMIinfo);
				pst.setString(1, user);
				ResultSet rs = pst.executeQuery();
				displayActivity(rs);
			} catch (SQLException ex) {
				System.out.println(ex.getMessage());
			}
		} else if (option == 3) {
			String BMIinfo = "SELECT * FROM users WHERE username = ?";
			try {
				PreparedStatement pst = conn.prepareStatement(BMIinfo);
				pst.setString(1, user);
				ResultSet rs = pst.executeQuery();
				displayUser(rs);
			} catch (SQLException ex) {
				System.out.println(ex.getMessage());
			}
		} else {
			System.out.println("error");
			return;
		}

	}
//DISPLAYES BMI LOG OF USER FROM BMI TABLE USING RESULTSET
	public void displayBMI(ResultSet rs) throws SQLException {
		System.out.println("\nUsername \t Weight \t Height \t BMI");
		while (rs.next()) {
			System.out.println(rs.getString("username") + "\t\t" + rs.getDouble("weight") + "\t\t" + rs.getDouble("height")
					+ "\t\t" + rs.getDouble("bmi"));
		}
	}
	//DISPLAYS ACTIVITY LOG FOR USER FROM ACTIVITY TABLE USING RESULTSET
	public void displayActivity( ResultSet rs) throws SQLException {
		System.out.println("username \t Activity Name \t Time Elapsed \t Calories Burned");
		while (rs.next()) {
			System.out.println(rs.getString("username") + "\t\t" + rs.getString("activity") + "\t\t"
					+ rs.getInt("time_elapsed_minutes") + "\t\t" + rs.getInt("calories_burned"));
		}
	}

	//DISPLAYS USER INFORMATION FROM USERS TABLE USING THE RESULTSET
	public void displayUser(ResultSet rs) throws SQLException {
		System.out.println("\nUsername \t\t First Name \t\t Last Name");
		while (rs.next()) {
			System.out.println(
					rs.getString("username") + "\t\t" + rs.getString("first_name") + "\t\t" + rs.getString("last_name"));
		}
	}

	/*
	 * MAIN METHOD Runs menus for user
	 */
	public static void main(String[] args) throws SQLException, IOException {
		postgresqlConn conn = new postgresqlConn();
		Connection c = conn.connect();
		conn.createtables(c);
		File users = new File("./users.txt");
		File bmis = new File("./bmis.txt");
		File activity = new File("./activity.txt");
		Scanner bmisc = new Scanner(bmis);
		Scanner activitysc = new Scanner(activity);
		Scanner readin = new Scanner(users);
		insertusers(c, readin);
		insertBMIs(c, bmisc );
		insertActivity(c, activitysc);

		Scanner sc = new Scanner(System.in);
		boolean loggedIn = false;
		String username = null;
		String password = null;

		while (!loggedIn) {
			System.out.println("Enter '1' to login or '2' to create new user: ");
			int menu1Option = sc.nextInt();

			if (menu1Option == 1) {
				System.out.println("Username:");
				username = sc.next();
				System.out.println("password:");
				password = sc.next();
				loggedIn = login(c, username, password);
			} else if (menu1Option == 2) {
				System.out.println("enter first name");
				String first = sc.next();
				System.out.println("enter Last name");
				String last = sc.next();
				boolean userExists = true;

				while (userExists) {
					System.out.println("enter Username");
					username = sc.next();
					userExists = conn.checkUsername(c, username);
				}
				System.out.println("enter password");
				password = sc.next();
				loggedIn = conn.insertuser(c, first, last, username, password);
			} else
				System.out.println("invalid input");

		}

		while (loggedIn) {
			System.out.println(
					"Enter     '1' to Update user info \n\t '2' Check BMI \n\t '3' to add an Activity Entry \n\t '4' to get data \n\t '5' to log out");
			int menu2Option = sc.nextInt();
			switch (menu2Option) {
			case 1:
				System.out.println("Enter '1' to edit username, \n\t '2' to edit password, \n\t'3' to delete user");
				int x = sc.nextInt();
				if(x == 1 ) {
				username = conn.updateUsername(c, username, password, sc);
				break;}
				else if(x ==2){
					conn.updatePassword(c, username, password, sc);
					break;
				}
				else {
					conn.deleteUser(c, username);
					break;
				}

			case 2:
				System.out.println("Enter weight in lbs:");
				double weight = sc.nextInt();
				System.out.println("Enter height in inches:");
				double height = sc.nextInt();
				conn.checkBMI(c, username, weight, height, sc);
				break;
			case 3:
				System.out.println("Enter name of Activity:");
				String act = sc.next();
				System.out.println("Enter '1' to log cardio activity, \n\t '2' for strength activity :");
				int type = sc.nextInt();
				System.out.println("Enter time elapsed in minutes");
				int time = sc.nextInt();
				conn.addActivity(c, username, act, type, time);
				break;
			case 4:
				System.out.println("Enter '1' to get BMI log, \n\t '2' to get Activity log, \n\t '3' for user info");
				int opt = sc.nextInt();
				conn.getInfo(c, username, opt);
				break;
			case 5:
				loggedIn = false;
				break;
			}
		}

		sc.close();
		System.exit(0);
	}

}
