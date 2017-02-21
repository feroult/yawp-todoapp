package cloudsqlsync.models.syncable;

import com.google.appengine.api.taskqueue.DeferredTask;
import io.yawp.commons.utils.JsonUtils;

public class SynchronizationTask implements DeferredTask {

    private String json;

    private Version version;

    public SynchronizationTask(Syncable entity, Version version) {
        this.json = JsonUtils.to(entity);
        this.version = version;
    }

    @Override
    public void run() {
        if (!isRightVersion()) {
            return;
        }

        
    }

    private boolean isRightVersion() {
        Version currentVersion = version.id.fetch();

        if (currentVersion.version > version.version) {
            return false;
        }

        if (currentVersion.version < version.version) {
            throw new RuntimeException("stale read, wait the right version");
        }

        return true;
    }
}
