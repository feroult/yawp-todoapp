package cloudsqlsync.models.syncable;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.TaskOptions;
import io.yawp.driver.appengine.pipes.utils.QueueHelper;
import io.yawp.repository.IdRef;
import io.yawp.repository.hooks.Hook;
import io.yawp.repository.models.ObjectHolder;
import io.yawp.repository.query.NoResultException;

public class SyncableHooks extends Hook<Syncable> {

    @Override
    public void beforeSave(Syncable entity) {
        if (!yawp.isTransationInProgress()) {
            yawp.begin();
        }
    }

    @Override
    public void afterSave(Syncable entity) {
        Version version = saveNewVersionMarker(entity);
        addSynchronizationTask(entity, version);
        yawp.commit();
    }

    private void addSynchronizationTask(Syncable entity, Version version) {
        Queue queue = QueueHelper.getDefaultQueue();
        queue.add(TaskOptions.Builder.withPayload(new SynchronizationTask(entity, version)));
    }

    private Version saveNewVersionMarker(Syncable entity) {
        ObjectHolder holder = new ObjectHolder(entity);

        IdRef<?> entityId = holder.getId();
        IdRef<Version> versionId = entityId.createChildId(Version.class, 1L);

        Version version;
        try {
            version = versionId.fetch();
        } catch (NoResultException e) {
            version = new Version();
            version.id = versionId;
            version.entityId = entityId;
        }

        version.version++;
        yawp.save(version);
        return version;
    }

}
