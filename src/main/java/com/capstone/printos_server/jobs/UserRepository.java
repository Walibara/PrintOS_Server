package com.capstone.printos_server.users;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByCognitoSub(String cognitoSub);
}

//Added this to work with the new db and cognito (Maria)
