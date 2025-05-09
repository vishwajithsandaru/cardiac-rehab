package com.dexter.fyp.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dexter.fyp.backend.entity.AppUser;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    
    public Optional<AppUser> findByEmail(String email);
    public boolean existsByEmail(String email);
    
}
