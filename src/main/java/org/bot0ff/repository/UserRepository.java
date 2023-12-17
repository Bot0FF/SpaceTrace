package org.bot0ff.repository;

import org.bot0ff.dto.UserDTO;
import org.bot0ff.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

@Transactional
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Boolean existsByUsername(String username);

    Optional<User> findRefreshTokenByUsername(String username);

    @Modifying
    @Query(value = "UPDATE users SET refresh_token = :refreshToken WHERE username = :username", nativeQuery = true)
    void setUserRefreshToken(@Param("refreshToken") String refreshToken, @Param("username") String username);

    @Modifying
    @Query(value = "SELECT token=:token FROM users WHERE username = :username", nativeQuery = true)
    String tokenIsBlocked(@Param("token") Date token, @Param("username") String username);

    @Query(value = "SELECT username, x, y, hp, mana FROM users WHERE username = :username", nativeQuery = true)
    Optional<UserDTO> findUserByName(@Param("username")String username);

    @Modifying
    @Query(value = "UPDATE users SET x = :x, y = :y, location = :location WHERE username = :username", nativeQuery = true)
    void saveNewUserPosition(@Param("x") int x, @Param("y")int y, @Param("location")Long location, @Param("username")String name);
}
