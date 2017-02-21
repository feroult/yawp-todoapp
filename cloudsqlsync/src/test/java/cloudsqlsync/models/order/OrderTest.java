package cloudsqlsync.models.order;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import cloudsqlsync.utils.EndpointTestCase;

public class OrderTest extends EndpointTestCase {

    @Test
    public void testCreate() {
        // TODO Auto-generated method stub
        String json = post("/orders", "{}");
        Order order = from(json, Order.class);

        assertNotNull(order);
    }

}
