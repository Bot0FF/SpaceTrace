package org.bot0ff.repository;

import org.bot0ff.entity.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional
@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {

    @Query(value = "SELECT * FROM players WHERE name = :username", nativeQuery = true)
    Optional<Player> findByName(@Param("username")String username);

    @Modifying
    @Query(value = "UPDATE players SET posX = :posX, posY = :posY WHERE name = :name", nativeQuery = true)
    void saveNewUserPosition(@Param("posX") int posX, @Param("posY")int posY, @Param("name")String name);
}
