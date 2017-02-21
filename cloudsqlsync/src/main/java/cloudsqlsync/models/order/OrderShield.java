package cloudsqlsync.models.order;

import io.yawp.repository.shields.Shield;

public class OrderShield extends Shield<Order> {

    @Override
    public void defaults() {
        // TODO Auto-generated method stub
        allow();
    }

}
