package com.densoft.shopmeAdmin.product;

import com.densoft.shopmecommon.entity.product.Product;
import com.densoft.shopmecommon.exception.ProductNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class ProductRestController {
    @Autowired
    private ProductService productService;


    @PostMapping("/products/check_unique")
    public String checkUnique(@RequestParam(value = "id", required = false) Integer id, @RequestParam("name") String name) {
        return productService.checkUnique(id, name);
    }


    @GetMapping("/products/get/{id}")
    public ProductDTO getProductInfo(@PathVariable("id") Integer id)
            throws ProductNotFoundException {
        Product product = productService.get(id);
        return new ProductDTO(product.getName(), product.getMainImagePath(),
                product.getDiscountPrice(), product.getCost());
    }
}
