package cloudsqlsync.models.syncable;

import io.yawp.repository.hooks.Hook;

public class SyncableCloudSQLHook extends Hook<Syncable> {

    @Override
    public void beforeSave(Syncable entity) {
        if (!yawp.isTransationInProgress()) {
            yawp.begin();
        }
    }

    @Override
    public void afterSave(Syncable entity) {
        feature(SynchronizationHelper.class).enqueueTaskFor(entity);
        yawp.commit();
    }

}
