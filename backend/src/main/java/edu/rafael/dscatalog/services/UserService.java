package edu.rafael.dscatalog.services;

import edu.rafael.dscatalog.dto.RoleDTO;
import edu.rafael.dscatalog.dto.UserDTO;
import edu.rafael.dscatalog.dto.UserInsertDTO;
import edu.rafael.dscatalog.dto.UserUpdateDTO;
import edu.rafael.dscatalog.entities.Role;
import edu.rafael.dscatalog.entities.User;
import edu.rafael.dscatalog.repositories.RoleRepository;
import edu.rafael.dscatalog.repositories.UserRepository;
import edu.rafael.dscatalog.services.exceptions.DatabaseException;
import edu.rafael.dscatalog.services.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    BCryptPasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public List<UserDTO> findAll(){
         List<User> users = userRepository.findAll();
         return users.stream().map(UserDTO::new).toList();
    }

    @Transactional(readOnly = true)
    public Page<UserDTO> findAllPaged(Pageable pageable){
        Page<User> users = userRepository.findAll(pageable);
        return users.map(UserDTO::new);
    }

    @Transactional(readOnly = true)
    public UserDTO findById(Long id){
        User user = userRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Produto não encontrada")
        );
        return new UserDTO(user);
    }

    @Transactional
    public UserDTO insert(UserInsertDTO dto){
        User user = new User();
        copyDtoToEntity(dto, user);
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user = userRepository.save(user);
        return new UserDTO(user);
    }

    @Transactional
    public UserDTO update(Long id, UserUpdateDTO dto){
        try{
            User user = userRepository.getReferenceById(id);
            copyDtoToEntity(dto, user);
            user = userRepository.save(user);
            return new UserDTO(user);
        }catch (EntityNotFoundException e){
            throw new ResourceNotFoundException("Id: "+id+" não encontrado");
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("Produto não encontrado");
        }
        try {
            userRepository.deleteById(id);
        }
        catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Falha de integridade referencial");
        }
    }

    private void copyDtoToEntity(UserDTO dto, User entity){
        entity.setFirstName(dto.getFirstName());
        entity.setLastName(dto.getLastName());
        entity.setEmail(dto.getEmail());
        for(RoleDTO roleDTO: dto.getRoles()){
            Role role = roleRepository.getReferenceById(roleDTO.getId());
            entity.getRoles().add(role);
        }
    }
}
