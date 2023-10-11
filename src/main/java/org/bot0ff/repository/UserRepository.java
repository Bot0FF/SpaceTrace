package org.bot0ff.repository;

import org.bot0ff.dto.jpa.MoveUser;
import org.bot0ff.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Boolean existsByUsername(String username);

    @Query(value = "SELECT posX, posY FROM users WHERE username = :username", nativeQuery = true)
    List<MoveUser> getPosxAndPosyByUserName(@Param("username")String username);

    @Modifying
    @Query(value = "UPDATE users SET posX = :posX, posY = :posY WHERE username = :username", nativeQuery = true)
    void saveNewUserPosition(@Param("posX") int posX, @Param("posY")int posY, @Param("username")String username);
}
