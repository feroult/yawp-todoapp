package demo.models.product;

import io.yawp.repository.IdRef;
import io.yawp.repository.annotations.Endpoint;
import io.yawp.repository.annotations.Id;

@Endpoint(path = "/products")
public class Product {

    @Id
    IdRef<Product> id;

    String name;

}
