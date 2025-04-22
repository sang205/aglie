package connectDB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 *
 * @author atuandev
 */
public class JDBCConnection {

    static String url = "jdbc:sqlserver://localhost:1433;databasename=QLHIEUTHUOC;encrypt=true;trustServerCertificate=true;";
    static String user = "sa";
    static String password = "sang2005";

    public static PreparedStatement getStmt(String sql, Object... args) throws Exception {
        Connection con = DriverManager.getConnection(url, user, password);
        PreparedStatement stmt;
        if (sql.trim().startsWith("{")) {
            stmt = con.prepareCall(sql);
        } else {
            stmt = con.prepareStatement(sql);
        }

        for (int i = 0; i < args.length; i++) {
            stmt.setObject(i + 1, args[i]);
        }
        return stmt;
    }

    public static int update(String sql, Object... args) {
        try {
            PreparedStatement stmt = JDBCConnection.getStmt(sql, args);
            try {
                return stmt.executeUpdate();
            } finally {
                stmt.getConnection().close();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static ResultSet query(String sql, Object... args) throws Exception {
        PreparedStatement stmt = JDBCConnection.getStmt(sql, args);
        return stmt.executeQuery();
    }

    public static void main(String[] args) {

        try {
           
            Connection conn = DriverManager.getConnection(url, user, password);
            if (conn != null) {
                System.out.println("✅ Kết nối SQL Server thành công!");
                conn.close();
            } else {
                System.out.println("❌ Kết nối thất bại!");
            }
        } catch (Exception e) {
            System.out.println("❌ Lỗi khi kết nối:");
            e.printStackTrace();
        }
    }
}
