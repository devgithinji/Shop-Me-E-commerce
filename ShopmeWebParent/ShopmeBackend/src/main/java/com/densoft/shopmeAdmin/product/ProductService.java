package com.densoft.shopmeAdmin.product;

import com.densoft.shopmecommon.entity.Product;
import com.densoft.shopmecommon.exception.ProductNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    public static final int PRODUCTS_PER_PAGE = 5;
    @Autowired
    private ProductRepository productRepository;

    public List<Product> listAll() {
        return productRepository.findAll();
    }


    public Page<Product> listByPage(int pageNum, String sortField, String sortDir, String keyWord, Integer categoryId) {
        Sort sort = Sort.by(sortField);

        sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? sort.ascending() : sort.descending();

        Pageable pageable = PageRequest.of(pageNum - 1, PRODUCTS_PER_PAGE, sort);
        if (keyWord != null && !keyWord.isEmpty()) {
            if (categoryId != null && categoryId > 0) {
                String categoryIdMatch = "-" + categoryId + "-";
                return productRepository.searchInCategory(categoryId, categoryIdMatch, keyWord, pageable);
            }
            return productRepository.findAll(keyWord, pageable);
        }

        if (categoryId != null && categoryId > 0) {
            String categoryIdMatch = "-" + categoryId + "-";
            return productRepository.findAllInCategory(categoryId, categoryIdMatch, pageable);
        }
        return productRepository.findAll(pageable);
    }

    public Product save(Product product) {
        if (product.getAlias() == null || product.getAlias().isEmpty()) {
            String alias = product.getName().replaceAll(" ", "_");
            product.setAlias(alias);
        } else {
            product.setAlias(product.getAlias().replaceAll(" ", "_"));
        }

        return productRepository.save(product);
    }

    public void saveProductPrice(Product productInForm) throws ProductNotFoundException {
        Optional<Product> optionalProduct = productRepository.findById(productInForm.getId());
        if (optionalProduct.isPresent()) {
            Product product = optionalProduct.get();
            product.setCost(productInForm.getCost());
            product.setPrice(productInForm.getPrice());
            product.setDiscountPercent(productInForm.getDiscountPercent());
            productRepository.save(product);
        } else {
            throw new ProductNotFoundException("Product with ID: " + productInForm.getId() + " not found");
        }
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

    public void delete(Integer id) throws ProductNotFoundException {
        Optional<Product> optionalProduct = productRepository.findById(id);
        if (optionalProduct.isPresent()) {
            productRepository.deleteById(id);
        } else {
            throw new ProductNotFoundException("Product with ID: " + id + " not found");
        }
    }

    public Product get(Integer id) throws ProductNotFoundException {
        Optional<Product> optionalProduct = productRepository.findById(id);
        if (optionalProduct.isPresent()) {
            return optionalProduct.get();
        }
        throw new ProductNotFoundException("Product with ID: " + id + " not found");
    }

}
