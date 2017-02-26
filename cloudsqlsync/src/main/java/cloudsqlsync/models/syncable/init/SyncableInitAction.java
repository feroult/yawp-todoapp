package cloudsqlsync.models.syncable.init;

import cloudsqlsync.models.syncable.Syncable;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.TaskOptions;
import io.yawp.commons.http.annotation.GET;
import io.yawp.driver.appengine.pipes.utils.QueueHelper;
import io.yawp.repository.actions.Action;

public class SyncableInitAction extends Action<Syncable> {

    @GET
    public void syncInit() {
        Queue queue = QueueHelper.getDefaultQueue();
        queue.add(TaskOptions.Builder.withPayload(new FanoutDeferredTask(requestContext.getEndpointClazz())));
    }

}
