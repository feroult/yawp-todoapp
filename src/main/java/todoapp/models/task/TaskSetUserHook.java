package todoapp.models.task;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import io.yawp.repository.hooks.Hook;

public class TaskSetUserHook extends Hook<Task> {

    @Override
    public void beforeShield(Task task) {
        if (!userService().isUserLoggedIn()) {
            return;
        }
        task.setUser(userService().getCurrentUser().getEmail());
    }

    private UserService userService() {
        return UserServiceFactory.getUserService();
    }

}
