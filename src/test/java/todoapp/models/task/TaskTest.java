package todoapp.models.task;

import io.yawp.testing.appengine.AppengineTestHelper;
import org.junit.Before;
import org.junit.Test;

import todoapp.utils.EndpointTestCase;

import java.util.List;

import static org.junit.Assert.*;

public class TaskTest extends EndpointTestCase {

    @Before
    public void defaultLogin() {
        helper().login("default", "rock.com");
    }

    @Test
    public void testCreate() {
        String json = post("/tasks", "{ 'title': 'wash dishes' }");
        Task task = from(json, Task.class);

        assertEquals("wash dishes", task.getTitle());
    }

    @Test
    public void testCreateWithNotes() {
        String json = post("/tasks", "{ notes: ['note 1', 'note 2'] }");
        Task task = from(json, Task.class);

        assertEquals(2, task.getNotes().size());
        assertEquals("note 1", task.getNotes().get(0));
        assertEquals("note 2", task.getNotes().get(1));
    }

    @Test
    public void testMarkAsDone() {
        post("/tasks/1", "{ }");
        assertFalse(yawp(Task.class).fetch(1l).isDone());

        put("/tasks/1/done");
        assertTrue(yawp(Task.class).fetch(1l).isDone());
    }

    @Test
    public void testPrivacy() {
        helper().login("janes", "rock.com");
        post("/tasks", "{ title: 'janes task' }");

        List<Task> tasks1 = fromList(get("/tasks"), Task.class);
        assertEquals(1, tasks1.size());
        assertEquals("janes task", tasks1.get(0).getTitle());

        helper().login("jim", "rock.com");
        List<Task> tasks2 = fromList(get("/tasks"), Task.class);
        assertEquals(0, tasks2.size());
    }

    private AppengineTestHelper helper() {
        return (AppengineTestHelper) helper;
    }

}