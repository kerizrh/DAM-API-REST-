package edu.sv.ues.dam235.apirestdemo.services;

import edu.sv.ues.dam235.apirestdemo.dtos.ProductsDTO;
import java.util.List;

public interface ProductServices {
    List<ProductsDTO> getAllProducts();
    ProductsDTO getProductById(Integer code);
    ProductsDTO saveProduct(ProductsDTO productDTO);
    ProductsDTO updateProduct(Integer code, ProductsDTO productDTO);
    boolean deleteProduct(Integer code);
}
