package edu.rafael.dscatalog.services;

import edu.rafael.dscatalog.dto.CategoryDTO;
import edu.rafael.dscatalog.entities.Category;
import edu.rafael.dscatalog.repositories.CategoryRepository;
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
public class CategoryService {
    @Autowired
    CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public List<CategoryDTO> findAll(){
         List<Category> categories = categoryRepository.findAll();
         return categories.stream().map(CategoryDTO::new).toList();
    }

    @Transactional(readOnly = true)
    public Page<CategoryDTO> findAllPaged(Pageable pageable){
        Page<Category> categories = categoryRepository.findAll(pageable);
        return categories.map(CategoryDTO::new);
    }

    @Transactional(readOnly = true)
    public CategoryDTO findById(Long id){
        Category category = categoryRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Categoria não encontrada")
        );
        return new CategoryDTO(category);
    }

    @Transactional
    public CategoryDTO insert(CategoryDTO dto){
        Category category = new Category(dto.getName());
        category = categoryRepository.save(category);
        return new CategoryDTO(category);
    }

    @Transactional
    public CategoryDTO update(Long id, CategoryDTO dto){
        try{
            Category category = categoryRepository.getReferenceById(id);
            category.setName(dto.getName());
            category = categoryRepository.save(category);
            return new CategoryDTO(category);
        }catch (EntityNotFoundException e){
            throw new ResourceNotFoundException("Id: "+id+" não encontrado");
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public void delete(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Categoria não encontrado");
        }
        try {
            categoryRepository.deleteById(id);
        }
        catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Falha de integridade referencial");
        }
    }
}
