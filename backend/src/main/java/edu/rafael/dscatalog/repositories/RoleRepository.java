package edu.rafael.dscatalog.repositories;

import edu.rafael.dscatalog.entities.Role;
import edu.rafael.dscatalog.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
}
