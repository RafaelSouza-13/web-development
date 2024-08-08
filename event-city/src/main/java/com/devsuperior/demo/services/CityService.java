package com.devsuperior.demo.services;

import com.devsuperior.demo.comparators.CityComparatorByName;
import com.devsuperior.demo.dto.CityDTO;
import com.devsuperior.demo.entities.City;
import com.devsuperior.demo.exceptions.DatabaseException;
import com.devsuperior.demo.exceptions.ResourceNotFoundException;
import com.devsuperior.demo.repositories.CityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CityService {
    @Autowired
    CityRepository repository;

    @Transactional(readOnly = true)
    public List<CityDTO> findAll(){
        List<City> entities = repository.findAll();
        entities.sort(new CityComparatorByName());
        return entities.stream().map(CityDTO::new).toList();
    }

    @Transactional
    public CityDTO insert(CityDTO dto) {
        City entity = new City();
        cityDtoToCity(dto, entity);
        entity = repository.save(entity);
        return new CityDTO(entity);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public void delete(Long id) {
        if(!repository.existsById(id)){
            throw new ResourceNotFoundException("Cidade não encontrada");
        }
        try {
            repository.deleteById(id);
        }
        catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Falha de integridade");
        }

    }

    private void cityDtoToCity(CityDTO dto, City entity){
        entity.setName(dto.getName());
    }


}
