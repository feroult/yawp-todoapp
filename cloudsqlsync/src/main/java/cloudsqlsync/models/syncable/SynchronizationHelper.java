package cloudsqlsync.models.syncable;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.TaskOptions;
import io.yawp.driver.appengine.pipes.utils.QueueHelper;
import io.yawp.repository.Feature;
import io.yawp.repository.IdRef;
import io.yawp.repository.models.ObjectHolder;
import io.yawp.repository.query.NoResultException;

public class SynchronizationHelper extends Feature {

    public void enqueueTaskFor(Object entity) {
        Queue queue = QueueHelper.getDefaultQueue();
        queue.add(TaskOptions.Builder.withPayload(new SynchronizationDeferredTask(entity, newVersion(entity))));
    }

    private Version newVersion(Object entity) {
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

}
