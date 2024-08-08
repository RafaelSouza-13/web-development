package com.devsuperior.demo.services;

import com.devsuperior.demo.dto.CityDTO;
import com.devsuperior.demo.dto.EventDTO;
import com.devsuperior.demo.entities.City;
import com.devsuperior.demo.entities.Event;
import com.devsuperior.demo.exceptions.ResourceNotFoundException;
import com.devsuperior.demo.repositories.CityRepository;
import com.devsuperior.demo.repositories.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EventService {
    @Autowired
    EventRepository repository;
    @Autowired
    CityRepository cityRepository;

    public EventDTO update(Long id, EventDTO dto) {
        if(!repository.existsById(id)){
            throw new ResourceNotFoundException("Evento não encontrado");
        }
        Event entity = repository.getReferenceById(id);
        eventDtoToEvent(dto, entity);
        entity.setCity(cityRepository.getReferenceById(dto.getCityId()));
        entity = repository.save(entity);
        return new EventDTO(entity);
    }

    private void eventDtoToEvent(EventDTO dto, Event entity){
        entity.setName(dto.getName());
        entity.setDate(dto.getDate());
        entity.setUrl(dto.getUrl());
    }
}
