import yawp from 'yawp';
import {extendObservable, observe} from 'mobx';

// only for dev
yawp.config(c => {
    c.baseUrl('http://localhost:8080/api');
});

class Product extends yawp('/products') {

    constructor(attrs) {
        super();
        extendObservable(this, attrs);
        observe(this, () => this.save());
    }

}

export default Product;
