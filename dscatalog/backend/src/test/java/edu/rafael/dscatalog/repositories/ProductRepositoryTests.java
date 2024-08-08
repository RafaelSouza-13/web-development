package edu.rafael.dscatalog.repositories;

import edu.rafael.dscatalog.entities.Product;
import edu.rafael.dscatalog.tests.Factory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

@DataJpaTest
public class ProductRepositoryTests {
    @Autowired
    private ProductRepository repository;
    private long existId;
    private long nonExistsId;
    private long countTotalProducts;

    @BeforeEach
    void setUp(){
        existId = 1L;
        nonExistsId = 1000L;
        countTotalProducts = 25L;
    }

    @Test
    public void deleteShouldDeleteObjectWhenIdExists(){
        repository.deleteById(existId);
        Optional<Product> result = repository.findById(existId);
        Assertions.assertFalse(result.isPresent());
    }

    @Test
    public void saveShouldPersistWithAutoincrement(){
        Product product = Factory.createProduct();
        product.setId(null);
        product = repository.save(product);
        Assertions.assertNotNull(product.getId());
        Assertions.assertEquals(countTotalProducts + 1, product.getId());
    }

    @Test
    public void findByIdShouldReturnOptionalNotEmptyWhenIdExists(){
        Optional<Product> product = repository.findById(existId);
        Assertions.assertTrue(product.isPresent());
    }

    @Test
    public void findByIdShouldReturnOptionalEmptyWhenIdNotExists(){
        Optional<Product> product = repository.findById(nonExistsId);
        Assertions.assertFalse(product.isPresent());
    }

}
