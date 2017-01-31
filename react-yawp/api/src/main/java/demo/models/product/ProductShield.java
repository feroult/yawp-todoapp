package demo.models.product;

import io.yawp.repository.shields.Shield;

public class ProductShield extends Shield<Product> {

    @Override
    public void defaults() {
        // TODO Auto-generated method stub
        allow();
    }

}
