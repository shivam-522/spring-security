package com.security.Security.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.security.Security.Entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findByUsername(String username);

	
}
