package todoapp.models.task;

import com.google.appengine.api.users.UserServiceFactory;
import io.yawp.repository.shields.Shield;

public class TaskShield extends Shield<Task> {

    @Override
    public void defaults() {
        allow().where("user", "=", currentUserEmail());
    }

    private String currentUserEmail() {
        return UserServiceFactory.getUserService().getCurrentUser().getEmail();
    }

}
