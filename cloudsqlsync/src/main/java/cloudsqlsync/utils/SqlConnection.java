package cloudsqlsync.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static io.yawp.commons.utils.Environment.isProduction;

public class SqlConnection {

    private Connection conn;

    private boolean error = false;

    public SqlConnection(Connection conn) {
        this.conn = conn;
    }

    public static SqlConnection newInstance() {
        return new SqlConnection(connect(false));
    }

    public static SqlConnection newInstance(boolean autoCommit) {
        return new SqlConnection(connect(autoCommit));
    }

    private static Connection connect(boolean autoCommit) {
        try {
            Connection conn = DriverManager.getConnection(getUrl());
            conn.setAutoCommit(autoCommit);
            return conn;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static String getUrl() {
        if (isProduction()) {
            try {
                Class.forName("com.mysql.jdbc.GoogleDriver");
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            return System.getProperty("cloudsql.url");
        }
        return System.getProperty("cloudsql.url.local");
    }

    public void run(SqlRunner block) {
        try {
            block.execute(conn);
        } catch (SQLException e) {
            handleError(e);
        }
    }

    private void handleError(SQLException e) {
        error = true;
        this.rollback();
        throw new RuntimeException(e);
    }

    private void rollback() {
        try {
            conn.rollback();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void commit() {
        if (error) {
            return;
        }
        try {
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
