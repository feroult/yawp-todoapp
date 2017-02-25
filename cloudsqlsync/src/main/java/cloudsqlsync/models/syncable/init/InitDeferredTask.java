package cloudsqlsync.models.syncable.init;

import cloudsqlsync.models.syncable.SynchronizationHelper;
import cloudsqlsync.models.syncable.Version;
import com.google.appengine.api.taskqueue.DeferredTask;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.TaskOptions;
import io.yawp.driver.appengine.pipes.utils.QueueHelper;
import io.yawp.repository.IdRef;
import io.yawp.repository.query.NoResultException;
import io.yawp.repository.query.QueryBuilder;

import java.util.List;

import static io.yawp.repository.Yawp.feature;
import static io.yawp.repository.Yawp.yawp;

public class InitDeferredTask implements DeferredTask {

    private static final int BATCH_SIZE = 100;

    private Class<?> endpointClazz;

    private String cursor;

    public InitDeferredTask(Class<?> endpointClazz) {
        this(endpointClazz, null);
    }

    public InitDeferredTask(Class<?> endpointClazz, String cursor) {
        this.endpointClazz = endpointClazz;
        this.cursor = cursor;
    }

    @Override
    public void run() {
        QueryBuilder<?> query = configureQuery();
        List<? extends IdRef<?>> ids = query.ids();

        fanout(ids, query.getCursor());
        executeBatch(ids);
    }

    private void executeBatch(List<? extends IdRef<?>> ids) {
        for (IdRef<?> id : ids) {
            IdRef<Version> versionId = id.createChildId(Version.class, 1l);
            if (alreadyExists(versionId)) {
                break;
            }

            yawp.begin();
            enqueueSynchronizationTask(id);
            yawp.commit();
        }
    }

    private void enqueueSynchronizationTask(IdRef<?> id) {
        Object entity;
        try {
            entity = id.fetch();
        } catch (NoResultException e) {
            return;
        }
        feature(SynchronizationHelper.class).enqueueTaskFor(entity);
    }

    private boolean alreadyExists(IdRef<Version> versionId) {
        try {
            versionId.fetch();
        } catch (NoResultException e) {
            return true;
        }
        return false;
    }

    private void fanout(List<?> ids, String cursor) {
        if (ids.size() < BATCH_SIZE) {
            return;
        }
        Queue queue = QueueHelper.getDefaultQueue();
        queue.add(TaskOptions.Builder.withPayload(new InitDeferredTask(endpointClazz, cursor)));

    }

    private QueryBuilder<?> configureQuery() {
        QueryBuilder<?> query = yawp(endpointClazz).limit(BATCH_SIZE);
        if (cursor != null) {
            query.cursor(cursor);
        }
        return query;
    }

}
