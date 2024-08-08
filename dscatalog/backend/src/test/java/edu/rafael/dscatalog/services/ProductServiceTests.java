package edu.rafael.dscatalog.services;

import edu.rafael.dscatalog.dto.ProductDTO;
import edu.rafael.dscatalog.entities.Product;
import edu.rafael.dscatalog.repositories.ProductRepository;
import edu.rafael.dscatalog.services.exceptions.DatabaseException;
import edu.rafael.dscatalog.services.exceptions.ResourceNotFoundException;
import edu.rafael.dscatalog.tests.Factory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.times;

@ExtendWith(SpringExtension.class)
public class ProductServiceTests {

    @InjectMocks
    private ProductService service;
    @Mock
    private ProductRepository repository;

    private long existsId;
    private long nonExistsId;
    private long dependentId;
    private PageImpl<Product> page;
    private Product product;
    private Product productReturn;

    @BeforeEach
    void setUp(){
        existsId = 1L;
        nonExistsId = 1000L;
        dependentId = 2L;
        product = Factory.createProduct();
        productReturn = Factory.productReturn();
        page = new PageImpl<>(List.of(product));

        Mockito.doNothing().when(repository).deleteById(existsId);
        Mockito.doThrow(DataIntegrityViolationException.class).when(repository).deleteById(dependentId);

        Mockito.when(repository.existsById(existsId)).thenReturn(true);
        Mockito.when(repository.existsById(nonExistsId)).thenReturn(false);
        Mockito.when(repository.existsById(dependentId)).thenReturn(true);

        Mockito.when(repository.findAll((Pageable)ArgumentMatchers.any())).thenReturn(page);
        Mockito.when(repository.save(ArgumentMatchers.any())).thenReturn(product);

        Mockito.when(repository.findById(existsId)).thenReturn(Optional.of(productReturn));

        Mockito.when(repository.findById(nonExistsId)).thenReturn(Optional.empty());
        Mockito.doThrow(ResourceNotFoundException.class).when(repository).findById(nonExistsId);
    }

    @Test
    public void deleteShouldDoNothingIdExists(){
        Assertions.assertDoesNotThrow(() -> {
            service.delete(existsId);
        });
        Mockito.verify(repository, times(1)).deleteById(existsId);
    }

    @Test
    public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.delete(nonExistsId);
        });
    }

    @Test
    public void deleteShouldThrowDataBaseExceptionWhenDependedId() {
        Assertions.assertThrows(DatabaseException.class, () -> {
            service.delete(dependentId);
        });
    }

    @Test
    public void findAllPagedShouldReturnPage(){
        Pageable pageable = PageRequest.of(0, 10);
        Page<ProductDTO> result = service.findAllPaged(pageable);

        Assertions.assertNotNull(result);
        Mockito.verify(repository, Mockito.times(1)).findAll(pageable);
    }

    @Test
    public void findByIdShouldReturnProductDTOWhenIdExist(){
        ProductDTO dto = service.findById(existsId);
        Assertions.assertNotNull(dto);
        Assertions.assertEquals(existsId, dto.getId());
    }

    @Test
    public void findByIdShouldThrowResourceNotFoundExceptionWhenIdNotExist(){
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.findById(nonExistsId);
        });
        Mockito.verify(repository, Mockito.times(1)).findById(nonExistsId);
    }

}
