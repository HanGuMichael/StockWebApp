package csci310;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SQL {
	public static boolean userExist (String username) { //returns a boolean for if a user with the username exists or not
		CreateUserTable c = new CreateUserTable();
		CreateStockTable c1 = new CreateStockTable();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		boolean ret = true;

		try {
			if(username.equals("ausernamethatisobviouslyillegal")) {
				conn = DriverManager.getConnection("throw exception please!");
			}
			conn = DriverManager.getConnection("jdbc:sqlite:project.db");
			ps = conn.prepareStatement("SELECT * FROM users WHERE username=?"); //prepare statement is for user input, use statement if not
			ps.setString(1, username); 
			rs = ps.executeQuery(); 

			if (!rs.next()) {
				return false;
			}

		} catch (SQLException sqle) {
			//System.out.println("sqle1: " + sqle.getMessage());
			ret = false;
		}
		
		
		try {
				if (rs != null) { rs.close(); }
				if (ps != null) { ps.close(); }
				if (conn != null) { conn.close(); }
			} catch (SQLException sqle) {
				//System.out.println("sqle2: " + sqle.getMessage());
			}

		return ret;
	}
	
	//---------------------------------------------------------------------------------------------
	
	public static boolean login(String username, String password) { //queries the database to authenticate a user login
		CreateUserTable c = new CreateUserTable();
		CreateStockTable c1 = new CreateStockTable();
		boolean foundUser = false;

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			if(username.equals("ausernamethatisobviouslyillegal")) {
				conn = DriverManager.getConnection("throw exception please!");
			}
			conn = DriverManager.getConnection("jdbc:sqlite:project.db");
			ps = conn.prepareStatement("SELECT * FROM users WHERE username=?"); 
			ps.setString(1, username); 
			rs = ps.executeQuery(); //gets the user

			if (!rs.next()) {
				return foundUser;
			}
			String pswd = rs.getString("password"); //checks the password
			password = PasswordHash.getHash(password);//hashing
			if (pswd.equals(password)) {
				foundUser = true;
			}

		} catch (SQLException sqle) {
			//System.out.println("sqle1: " + sqle.getMessage());
		}
		
			try {
				if (rs != null) { rs.close(); }
				if (ps != null) { ps.close(); }
				if (conn != null) { conn.close(); }
			} catch (SQLException sqle) {
				//System.out.println("sqle2: " + sqle.getMessage());
			}
		

		return foundUser;
	}
	
	//----------------------------------------------------------------------------------------
	
	public static void register(String username, String password) { //registers the user to the database
		CreateUserTable c = new CreateUserTable();
		CreateStockTable c1 = new CreateStockTable();
		//ideally we will have used userExists to check before this whether a user of that username exists.
		Connection conn = null;
		PreparedStatement ps = null;
		//ResultSet rs = null;

		try {
			if(username.equals("ausernamethatisobviouslyillegal")) {
				conn = DriverManager.getConnection("throw exception please!");
			}
			conn = DriverManager.getConnection("jdbc:sqlite:project.db");
			
			password = PasswordHash.getHash(password);//hashing
			
			String sql = "INSERT INTO users (username,password) VALUES ('" + username + "','" + password + "')";
			ps = conn.prepareStatement(sql); //prepare statement is for user input, use statement if not
			ps.execute(); 

		}
		catch (SQLException sqle) {
			//System.out.println("sqle1: " + sqle.getMessage());
		}
		
			try {
				
				if (ps != null) { ps.close(); }
				if (conn != null) { conn.close(); }
			} catch (SQLException sqle) {
				//System.out.println("sqle2: " + sqle.getMessage());
			}
		
	}
	
	//----------------------------------------------------------------------------------------
	
	public static void addStock(String username, Stocks stock) {
		CreateUserTable c = new CreateUserTable();
		CreateStockTable c1 = new CreateStockTable();
		String ticker = stock.getTicker();
		String dayPurchase = stock.getDayPurchase();
		String daySold = stock.getDaySold();
		int quantity = stock.getQuantity();
		
		Connection conn = null;
		PreparedStatement ps = null;
		PreparedStatement ps2 = null;
		ResultSet rs = null;
		
		try {
			if(username.equals("ausernamethatisobviouslyillegal")) {
				conn = DriverManager.getConnection("throw exception please!");
			}
			conn = DriverManager.getConnection("jdbc:sqlite:project.db");
			ps = conn.prepareStatement("SELECT * FROM users WHERE username=?"); 
			ps.setString(1, username); 
			rs = ps.executeQuery(); //gets the user

			rs.next();

			int userID = rs.getInt("userID");

			String insertStatement = "INSERT INTO stocks (ticker, dayPurchase, daySold, userID, quantity) VALUES "
					+ "('" + ticker + "', '" + dayPurchase + "', '" + daySold + "', '" + userID + "', '" + quantity + "' )";

			ps2 = conn.prepareStatement(insertStatement);
			//rs2 = ps2.executeQuery();
			ps2.execute();

		} catch (SQLException sqle) {
			System.out.println("sqle1: " + sqle.getMessage());
		}
		
		try {
			if (rs != null) { rs.close(); }
			if (ps != null) { ps.close(); }
			if (ps2 != null) {ps2.close(); }
			if (conn != null) { conn.close(); }
		} catch (SQLException sqle) {
			System.out.println("sqle2: " + sqle.getMessage());
		}	
	}
	
	//----------------------------------------------------------------------------------------
	
	public static void removeStock(String username, String ticker) {
		CreateUserTable c = new CreateUserTable();
		CreateStockTable c1 = new CreateStockTable();
		Connection conn = null;
		PreparedStatement ps = null;
		PreparedStatement ps2 = null;
		ResultSet rs = null;
		try {
			if(username.equals("ausernamethatisobviouslyillegal")) {
				conn = DriverManager.getConnection("throw exception please!");
			}
			conn = DriverManager.getConnection("jdbc:sqlite:project.db");
			ps = conn.prepareStatement("SELECT * FROM users WHERE username=?"); 
			ps.setString(1, username); 
			rs = ps.executeQuery(); //gets the user

			rs.next();

			int userID = rs.getInt("userID");
			ps2 = conn.prepareStatement("DELETE FROM stocks WHERE userID=? AND ticker=?");
			ps2.setInt(1, userID);
			ps2.setString(2, ticker);

			//rs2 = ps2.executeQuery();
			ps2.execute();

		} catch (SQLException sqle) {
			System.out.println("sqle1: " + sqle.getMessage());
		}
		
		try {
			if (rs != null) { rs.close(); }
			if (ps != null) { ps.close(); }
			if (ps2 != null) {ps2.close(); }
			if (conn != null) { conn.close(); }
		} catch (SQLException sqle) {
			System.out.println("sqle2: " + sqle.getMessage());
		}	
		
	}
	

}
