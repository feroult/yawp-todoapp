import React, {Component} from 'react';
import {observer} from 'mobx-react';
import Product from './Product';

class App extends Component {

    product = new Product({name: ''});

    onChange = (event) => {
        this.product.name = event.target.value;
    };

    render() {
        return (
            <div style={{margin: 40}}>
                <form>
                    <input type="text" placeholder="name" value={this.product.name} onChange={this.onChange}/>
                </form>
            </div>
        );
    }
}

export default observer(App);
