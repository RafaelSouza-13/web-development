package edu.rafael.dscatalog.services;

import edu.rafael.dscatalog.dto.CategoryDTO;
import edu.rafael.dscatalog.dto.ProductDTO;
import edu.rafael.dscatalog.entities.Category;
import edu.rafael.dscatalog.entities.Product;
import edu.rafael.dscatalog.repositories.CategoryRepository;
import edu.rafael.dscatalog.repositories.ProductRepository;
import edu.rafael.dscatalog.services.exceptions.DatabaseException;
import edu.rafael.dscatalog.services.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductService {
    @Autowired
    ProductRepository productRepository;
    @Autowired
    CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public List<ProductDTO> findAll(){
         List<Product> products = productRepository.findAll();
         return products.stream().map(ProductDTO::new).toList();
    }

    @Transactional(readOnly = true)
    public Page<ProductDTO> findAllPaged(Pageable pageable){
        Page<Product> products = productRepository.findAll(pageable);
        return products.map(ProductDTO::new);
    }

    @Transactional(readOnly = true)
    public ProductDTO findById(Long id){
        Product product = productRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Produto não encontrada")
        );
        return new ProductDTO(product, product.getCategories());
    }

    @Transactional
    public ProductDTO insert(ProductDTO dto){
        Product product = new Product();
        copyDtoToEntity(dto, product);
        product = productRepository.save(product);
        return new ProductDTO(product);
    }

    @Transactional
    public ProductDTO update(Long id, ProductDTO dto){
        try{
            Product product = productRepository.getReferenceById(id);
            copyDtoToEntity(dto, product);
            product = productRepository.save(product);
            return new ProductDTO(product);
        }catch (EntityNotFoundException e){
            throw new ResourceNotFoundException("Id: "+id+" não encontrado");
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public void delete(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Produto não encontrado");
        }
        try {
            productRepository.deleteById(id);
        }
        catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Falha de integridade referencial");
        }
    }

    private void copyDtoToEntity(ProductDTO dto, Product entity){
        entity.setName(dto.getName());
        entity.setPrice(dto.getPrice());
        entity.setDate(dto.getDate());
        entity.setDescription(dto.getDescription());
        entity.setImgUrl(dto.getImgUrl());
        entity.getCategories().clear();
        for(CategoryDTO categoryDTO: dto.getCategories()){
            Category category = categoryRepository.getReferenceById(categoryDTO.getId());
            entity.getCategories().add(category);
        }
    }
}
