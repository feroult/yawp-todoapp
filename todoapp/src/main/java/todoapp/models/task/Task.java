package todoapp.models.task;

import io.yawp.repository.IdRef;
import io.yawp.repository.annotations.Endpoint;
import io.yawp.repository.annotations.Id;
import io.yawp.repository.annotations.Index;
import io.yawp.repository.annotations.Json;

import java.util.List;

@Endpoint(path = "/tasks")
public class Task {

    @Id
    private IdRef<Task> id;

    private String title;

    @Json
    private List<String> notes;

    @Index
    private String user;

    private boolean done;

    public String getTitle() {
        return title;
    }

    public List<String> getNotes() {
        return notes;
    }

    public void markAsDone() {
        this.done = true;
    }

    public boolean isDone() {
        return done;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
