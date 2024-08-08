package edu.rafael.dscatalog.repositories;

import edu.rafael.dscatalog.entities.Category;
import edu.rafael.dscatalog.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findByEmail(String Email);
}
