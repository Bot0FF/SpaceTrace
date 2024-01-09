package org.bot0ff.repository;

import org.bot0ff.entity.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {
    Optional<Player> findByName(String name);
    Boolean existsByName(String name);

    @Modifying
    @Query(value = "UPDATE players SET x = :x, y = :y, location = :location WHERE name = :name", nativeQuery = true)
    void saveNewPlayerPosition(@Param("x") int x, @Param("y")int y, @Param("location") Long location, @Param("name") String name);

}
