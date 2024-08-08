package edu.rafael.dscatalog.resources;


import edu.rafael.dscatalog.dto.UserDTO;
import edu.rafael.dscatalog.dto.UserInsertDTO;
import edu.rafael.dscatalog.dto.UserUpdateDTO;
import edu.rafael.dscatalog.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping(value = "/users")
public class UserResource {
    @Autowired
    UserService userService;

    @GetMapping
    public ResponseEntity<Page<UserDTO>> findAll(Pageable pageable){
        Page<UserDTO> pages = userService.findAllPaged(pageable);
        return ResponseEntity.ok(pages);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<UserDTO> findById(@PathVariable Long id){
        UserDTO dto = userService.findById(id);
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<UserDTO> insert(@Valid  @RequestBody UserInsertDTO dtoInsert){
        UserDTO dto = userService.insert(dtoInsert);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").
                buildAndExpand(dto.getId()).toUri();
        return ResponseEntity.created(uri).body(dto);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<UserDTO> update(@Valid @PathVariable Long id, @RequestBody UserUpdateDTO dto){
        UserDTO newDto = userService.update(id, dto);
        return ResponseEntity.ok(newDto);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
