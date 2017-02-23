package cloudsqlsync.models.syncable;

import cloudsqlsync.utils.SqlConnection;
import cloudsqlsync.utils.SqlRunner;
import com.google.appengine.api.taskqueue.DeferredTask;
import io.yawp.commons.utils.JsonUtils;
import io.yawp.repository.IdRef;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static io.yawp.repository.Yawp.yawp;

public class SynchronizationTask implements DeferredTask {

    private String entityUri;

    private final String entityKind;

    private String entityJson;

    private long taskVersion;

    public SynchronizationTask(Syncable entity, Version taskVersion) {
        this.entityUri = taskVersion.entityId.getUri();
        this.entityKind = taskVersion.entityId.getModel().getKind();
        this.entityJson = JsonUtils.to(entity);
        this.taskVersion = taskVersion.version;
    }

    @Override
    public void run() {
        if (!isRightVersion()) {
            return;
        }

        SqlConnection conn = SqlConnection.newInstance();
        try {

            createTableIfNotExists(conn);
            updateEntity(conn);

        } finally {
            conn.commit();
        }

    }

    private void updateEntity(SqlConnection conn) {
        final String sql = "insert into " + entityKind + " (id, entity) values (?, ?) on duplicate key update entity = ?";

        conn.run(new SqlRunner() {
            public void execute(Connection conn) throws SQLException {
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, entityUri);
                ps.setString(2, entityJson);
                ps.setString(3, entityJson);
                ps.executeUpdate();
            }
        });
    }

    private void createTableIfNotExists(SqlConnection conn) {
        final String sql = "create table if not exists " + entityKind + " (id varchar(128), entity JSON, primary key (id))";

        conn.run(new SqlRunner() {
            public void execute(Connection conn) throws SQLException {
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.executeUpdate();
            }
        });
    }

    private boolean isRightVersion() {
        Version currentVersion = versionId().fetch();

        if (currentVersion.version > taskVersion) {
            return false;
        }

        if (currentVersion.version < taskVersion) {
            throw new RuntimeException("stale read, wait the right version");
        }

        return true;
    }

    private IdRef<Version> versionId() {
        return IdRef.parse(yawp(), entityUri).createChildId(Version.class, 1l);
    }

}
