package cloudsqlsync.models.syncable;

import cloudsqlsync.utils.SqlConnection;
import com.google.appengine.api.taskqueue.DeferredTask;
import io.yawp.commons.utils.JsonUtils;
import io.yawp.repository.IdRef;

import static io.yawp.repository.Yawp.feature;
import static io.yawp.repository.Yawp.yawp;

public class SynchronizationDeferredTask implements DeferredTask {

    private String entityUri;

    private final String entityKind;

    private String entityJson;

    private long taskVersion;

    public SynchronizationDeferredTask(Object entity, Version taskVersion) {
        this.entityUri = taskVersion.getEntityUri();
        this.entityKind = taskVersion.getEntityKind();
        this.entityJson = JsonUtils.to(entity);
        this.taskVersion = taskVersion.getVersion();
    }

    @Override
    public void run() {
        if (!isRightVersion()) {
            return;
        }

        SynchronizationHelper helper = feature(SynchronizationHelper.class);

        SqlConnection conn = SqlConnection.newInstance();
        try {

            helper.createTableIfNotExists(conn, entityKind);
            helper.updateEntity(conn, entityKind, entityUri, entityJson, taskVersion);
            conn.commit();

        } finally {
            conn.close();
        }

    }

    private boolean isRightVersion() {
        Version currentVersion = versionId().fetch();

        if (currentVersion.getVersion() > taskVersion) {
            return false;
        }

        if (currentVersion.getVersion() < taskVersion) {
            throw new RuntimeException("stale read, wait the right version");
        }

        return true;
    }

    private IdRef<Version> versionId() {
        return IdRef.parse(yawp(), entityUri).createChildId(Version.class, 1l);
    }

}
