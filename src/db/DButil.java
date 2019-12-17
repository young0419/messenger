package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DButil {

	public static Connection getConnection() {
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
//			return DriverManager.getConnection("jdbc:oracle:thin:@DESKTOP-VMRMGFV:1521/XEPDB1", "sey", "sey");
			return DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521/XEPDB1", "sey", "sey");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void close(PreparedStatement pstmt, Connection conn) {
		try { if(pstmt != null) pstmt.close(); } catch (SQLException sqle) {}		
		try { if(conn != null) conn.close(); } catch (SQLException sqle) {}		
	}
	
	public static void close(ResultSet rs, PreparedStatement pstmt, Connection conn) {		
		try { if(rs != null) rs.close(); } catch (SQLException sqle) {}		
		try { if(pstmt != null) pstmt.close(); } catch (SQLException sqle) {}		
		try { if(conn != null) conn.close(); } catch (SQLException sqle) {}		
	}
	
}
