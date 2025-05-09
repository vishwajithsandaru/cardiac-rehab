package com.dexter.fyp.backend.repository;

import com.dexter.fyp.backend.entity.User;
import jakarta.validation.constraints.Email;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<User, Long> {

    User findUserByEmail(@Email(message = "Email should be valid") String email);
}
