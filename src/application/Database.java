package application;

import java.sql.*;
import java.util.ArrayList;

class Database {
	public int acc_num;

	public String fname;
	public String Lname;
	public int phone;
	public String acc_type;
	public String bankname;
	public String pin;
	public final String Database_name = "users";
	public final String Database_user = "root";
	public final String Database_pass = "";

	public Connection con;

	public boolean init() throws SQLException {
		try {
			Class.forName("com.mysql.jdbc.Driver");

			try {
				this.con = DriverManager.getConnection("jdbc:mysql://localhost:3306/" + Database_name, Database_user,
						Database_pass);
			} catch (SQLException e) {

				System.out.println("Error: Database Connection Failed ! Please check the connection Setting");

				return false;

			}

		} catch (ClassNotFoundException e) {

			e.printStackTrace();

			return false;
		}

		return true;
	}

	public void insert() {
		String sql = "INSERT INTO face_bio (acc_num,first_name,last_name,phone,acc_type ,bank,pin) VALUES (?, ?, ?, ?,?,?, ?)";

		PreparedStatement statement = null;
		try {
			statement = con.prepareStatement(sql);
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		try {

			statement.setInt(1, this.acc_num);
			statement.setString(2, this.fname);
			statement.setString(3, this.Lname);
			statement.setInt(4, this.phone);
			statement.setString(5, this.acc_type);
			statement.setString(6, this.bankname);
			statement.setString(7, this.pin);
			int rowsInserted = statement.executeUpdate();
			if (rowsInserted > 0) {
				System.out.println("A new face data was inserted successfully!");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public ArrayList<String> getUser(int inCode) throws SQLException {

		ArrayList<String> user = new ArrayList<String>();

		try {

			String sql = "select * from face_bio where acc_num=" + inCode + " limit 1";

			Statement s = con.createStatement();

			ResultSet rs = s.executeQuery(sql);

			while (rs.next()) {

				user.add(0, Integer.toString(rs.getInt(2)));
				user.add(1, rs.getString(3));
				user.add(2, rs.getString(4));
				user.add(3, Integer.toString(rs.getInt(5)));
				user.add(4, rs.getString(6));
				user.add(5, rs.getString(7));
				user.add(6, rs.getString(8));
			}

			con.close(); // closing connection
		} catch (Exception e) {
			e.getStackTrace();
		}
		return user;
	}

	public void db_close() throws SQLException
	{
		try {
			con.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public int getCode() {
		return acc_num;
	}

	public void setCode(int code) {
		this.acc_num = code;
	}

	public String getFname() {
		return fname;
	}

	public void setFname(String fname) {
		this.fname = fname;
	}

	public String getLname() {
		return Lname;
	}

	public void setLname(String lname) {
		Lname = lname;
	}

	public int getPhone() {
		return phone;
	}

	public void setPhone(int phone) {
		this.phone = phone;
	}

	public String getAcc_type() {
		return acc_type;
	}

	public void setAcc_type(String acc_type) {
		this.acc_type = acc_type;
	}

	public String getBank() {
		return bankname;
	}

	public void setBank(String bank) {
		this.bankname = bank;
	}
	public void setPin(String pin) {
		this.pin = pin;
	}

}
