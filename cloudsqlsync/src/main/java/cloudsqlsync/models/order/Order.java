package cloudsqlsync.models.order;

import cloudsqlsync.models.syncable.Syncable;
import io.yawp.repository.IdRef;
import io.yawp.repository.annotations.Endpoint;
import io.yawp.repository.annotations.Id;

import java.util.Date;

@Endpoint(path = "/orders")
public class Order implements Syncable {

    @Id
    IdRef<Order> id;

    String product;

    Long quantity;

    Date createdAt;

}
