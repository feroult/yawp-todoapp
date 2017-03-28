package cloudsqlsync.models.syncable;

import cloudsqlsync.utils.SqlConnection;
import cloudsqlsync.utils.SqlRunner;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.TaskOptions;
import io.yawp.driver.appengine.pipes.utils.QueueHelper;
import io.yawp.repository.Feature;
import io.yawp.repository.IdRef;
import io.yawp.repository.models.ObjectHolder;
import io.yawp.repository.query.NoResultException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SynchronizationHelper extends Feature {

    public void enqueueTaskFor(Object entity) {
        Queue queue = QueueHelper.getDefaultQueue();
        Version taskVersion = saveVersionMarker(entity);
        queue.add(TaskOptions.Builder.withPayload(new SynchronizationDeferredTask(entity, taskVersion)));
    }

    public Version saveVersionMarker(Object entity) {
        ObjectHolder holder = new ObjectHolder(entity);

        IdRef<?> entityId = holder.getId();
        IdRef<Version> versionId = entityId.createChildId(Version.class, 1L);

        Version version;
        try {
            version = versionId.fetch();
        } catch (NoResultException e) {
            version = new Version();
            version.setId(versionId);
            version.setEntityId(entityId);
        }

        version.inc();
        yawp.save(version);
        return version;
    }

    public void updateEntity(SqlConnection conn,
                             String entityKind, final
                             String entityUri, final
                             String entityJson,
                             final Long version) {
        final String sql = "insert into " + entityKind + " (id, entity, version) values (?, ?, ?) on duplicate key update entity = ?, version = ?";

        conn.run(new SqlRunner() {
            public void execute(Connection conn) throws SQLException {
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, entityUri);
                ps.setString(2, entityJson);
                ps.setLong(3, version);
                ps.setString(4, entityJson);
                ps.setLong(5, version);
                ps.executeUpdate();
            }
        });
    }

    public void createTableIfNotExists(SqlConnection conn, final String entityKind) {
        final String sql = "create table if not exists " + entityKind + " (id varchar(128), entity JSON, version integer, primary key (id))";

        conn.run(new SqlRunner() {
            public void execute(Connection conn) throws SQLException {
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.executeUpdate();
            }
        });
    }

}
