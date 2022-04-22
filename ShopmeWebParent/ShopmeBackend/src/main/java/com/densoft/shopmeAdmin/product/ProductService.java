package com.densoft.shopmeAdmin.product;

import com.densoft.shopmecommon.entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

    public String checkUnique(Integer id, String name) {
        boolean isCreatingNew = (id == null || id == 0);
        Product productByName = productRepository.findByName(name);
        if (isCreatingNew) {
            if (productByName != null) return "Duplicate";
        } else {
            if (productByName != null && productByName.getId() != id) {
                return "Duplicate";
            }
        }

        return "OK";
    }

    public void updateProductEnabledStatus(Integer id, boolean enabled) throws ProductNotFoundException {
        Optional<Product> optionalProduct = productRepository.findById(id);
        if (optionalProduct.isPresent()) {
            Product product = optionalProduct.get();
            product.setEnabled(enabled);
            productRepository.save(product);
        } else {
            throw new ProductNotFoundException("Product with ID: " + id + " not found");
        }

    }
}
