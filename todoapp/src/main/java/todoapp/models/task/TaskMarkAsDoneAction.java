package todoapp.models.task;

import io.yawp.commons.http.annotation.PUT;
import io.yawp.repository.IdRef;
import io.yawp.repository.actions.Action;

public class TaskMarkAsDoneAction extends Action<Task> {

    @PUT("done")
    public void done(IdRef<Task> id) {
        Task task = id.fetch();
        task.markAsDone();
        yawp.save(task);
    }

}
