package cloudsqlsync.models.syncable.init;

import com.google.appengine.api.taskqueue.DeferredTask;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.TaskOptions;
import io.yawp.driver.appengine.pipes.utils.QueueHelper;
import io.yawp.repository.IdRef;
import io.yawp.repository.query.QueryBuilder;

import java.util.List;

import static io.yawp.repository.Yawp.yawp;

public class FanoutDeferredTask implements DeferredTask {

    private static final int BATCH_SIZE = 10;

    private Class<?> endpointClazz;

    private String cursor;

    public FanoutDeferredTask(Class<?> endpointClazz) {
        this(endpointClazz, null);
    }

    public FanoutDeferredTask(Class<?> endpointClazz, String cursor) {
        this.endpointClazz = endpointClazz;
        this.cursor = cursor;
    }

    @Override
    public void run() {
        QueryBuilder<?> query = configureQuery();
        List<? extends IdRef<?>> ids = query.ids();

        fanout(ids, query.getCursor());
        enqueueBatch(ids);
    }

    private void enqueueBatch(List<? extends IdRef<?>> ids) {
        Queue queue = QueueHelper.getDefaultQueue();
        queue.add(TaskOptions.Builder.withPayload(new BatchDeferredTask(ids)));
    }

    private void fanout(List<?> ids, String cursor) {
        if (ids.size() < BATCH_SIZE) {
            return;
        }
        Queue queue = QueueHelper.getDefaultQueue();
        queue.add(TaskOptions.Builder.withPayload(new FanoutDeferredTask(endpointClazz, cursor)));

    }

    private QueryBuilder<?> configureQuery() {
        QueryBuilder<?> query = yawp(endpointClazz).limit(BATCH_SIZE);
        if (cursor != null) {
            query.cursor(cursor);
        }
        return query;
    }

}
