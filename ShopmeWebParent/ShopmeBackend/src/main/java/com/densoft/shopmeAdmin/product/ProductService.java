package com.densoft.shopmeAdmin.product;

import com.densoft.shopmecommon.entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    public List<Product> listAll() {
        return productRepository.findAll();
    }

    public Product save(Product product) {
        if (product.getAlias() == null || product.getAlias().isEmpty()) {
            String alias = product.getAlias().replaceAll(" ", "_");
            product.setAlias(alias);
        } else {
            product.setAlias(product.getAlias().replaceAll(" ", "_"));
        }

        return productRepository.save(product);
    }

}
