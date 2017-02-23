package cloudsqlsync.utils;

import java.sql.Connection;
import java.sql.SQLException;

public abstract class SqlRunner {

    public abstract void execute(Connection conn) throws SQLException;

}
