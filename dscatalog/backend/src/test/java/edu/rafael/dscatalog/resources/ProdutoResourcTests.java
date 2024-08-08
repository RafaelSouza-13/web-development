package edu.rafael.dscatalog.resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.rafael.dscatalog.dto.ProductDTO;
import edu.rafael.dscatalog.services.ProductService;
import edu.rafael.dscatalog.services.exceptions.DatabaseException;
import edu.rafael.dscatalog.services.exceptions.ResourceNotFoundException;
import edu.rafael.dscatalog.tests.Factory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

@WebMvcTest(ProductResource.class)
public class ProdutoResourcTests {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductService service;
    private ProductDTO productDTO;
    private PageImpl<ProductDTO> page;
    private Long existId;
    private Long nonExistId;
    private Long dependentId;

    @BeforeEach
    void setUp(){
        existId = 1L;
        nonExistId = 1000L;
        dependentId = 3L;
        productDTO = Factory.createProductDTO();
        page = new PageImpl<>(List.of(productDTO));
        Mockito.when(service.findAllPaged(ArgumentMatchers.any())).thenReturn(page);
        Mockito.when(service.findById(existId)).thenReturn(productDTO);
        Mockito.when(service.findById(nonExistId)).thenThrow(ResourceNotFoundException.class);

        Mockito.when(service.update(ArgumentMatchers.eq(existId), ArgumentMatchers.any())).thenReturn(productDTO);
        Mockito.when(service.update(ArgumentMatchers.eq(nonExistId), ArgumentMatchers.any())).thenThrow(ResourceNotFoundException.class);

        Mockito.doNothing().when(service).delete(existId);
        Mockito.doThrow(ResourceNotFoundException.class).when(service).delete(nonExistId);
        Mockito.doThrow(DatabaseException.class).when(service).delete(dependentId);

        Mockito.doReturn(productDTO).when(service).insert(ArgumentMatchers.any());
    }

    @Test
    public void insertShouldReturnCreatedProductDTOWhenIdExists() throws Exception{
        String jsonBody = objectMapper.writeValueAsString(productDTO);
        ResultActions result = mockMvc.perform(post("/products").
                content(jsonBody).
                contentType(MediaType.APPLICATION_JSON).
                accept(MediaType.APPLICATION_JSON));
        result.andExpect(status().isCreated());
    }

    @Test
    public void deleteShouldReturnNoContentWhenIdExists() throws Exception{
        ResultActions result = mockMvc.perform(delete("/products/{id}", existId));
        result.andExpect(status().isNoContent());
    }

    @Test
    public void deleteShouldReturnNotFoundWhenIdDoesNotExists() throws Exception{
        ResultActions result = mockMvc.perform(get("/products/{id}", nonExistId));
        result.andExpect(status().isNotFound());
    }

    @Test
    public void updateShouldReturnProductDTOWhenIdExist() throws Exception{
        String jsonBody = objectMapper.writeValueAsString(productDTO);
        ResultActions result = mockMvc.perform(put("/products/{id}", existId).
                content(jsonBody).
                contentType(MediaType.APPLICATION_JSON).
                accept(MediaType.APPLICATION_JSON));
        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.id").exists());
        result.andExpect(jsonPath("$.name").exists());
        result.andExpect(jsonPath("$.description").exists());
    }

    @Test
    public void updateShouldReturnNotFoundWhenIdDoesNotExist() throws Exception{
        String jsonBody = objectMapper.writeValueAsString(productDTO);
        ResultActions result = mockMvc.perform(put("/products/{id}", nonExistId).
                content(jsonBody).
                contentType(MediaType.APPLICATION_JSON).
                accept(MediaType.APPLICATION_JSON));
        result.andExpect(status().isNotFound());
    }

    @Test
    public void findAllShouldReturnPage() throws Exception{
        ResultActions result = mockMvc.perform(get("/products").
                accept(MediaType.APPLICATION_JSON));
        result.andExpect(status().isOk());

    }

    @Test
    public void findByIdReturnProductWhenFindByIdExists() throws Exception{
        ResultActions result = mockMvc.perform(get("/products/{id}", existId).
                accept(MediaType.APPLICATION_JSON));
        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.id").exists());
        result.andExpect(jsonPath("$.name").exists());
        result.andExpect(jsonPath("$.description").exists());
    }

    @Test
    public void findByIdReturnNotFoundWhenFindByIdDoesNotExists() throws Exception{
        ResultActions result = mockMvc.perform(get("/products/{id}", nonExistId).
                accept(MediaType.APPLICATION_JSON));
        result.andExpect(status().isNotFound());
    }
}
