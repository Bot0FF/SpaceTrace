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

    //сохраняет новую позицию
    @Modifying
    @Query(value = "UPDATE players SET x = :x, y = :y, location = :location WHERE id = :id", nativeQuery = true)
    void saveNewPlayerPosition(@Param("x") int x, @Param("y")int y, @Param("location") Long location, @Param("id") Long id);

    //сохраняет новое сражение
    @Modifying
    @Query(value = "UPDATE players SET status = :status, fight = :fight WHERE id = :id", nativeQuery = true)
    void saveNewPlayerFightId(@Param("status") String status, @Param("fight") Long fight, @Param("id") Long id);

    //сбрасывает настройки сражения player
    @Modifying
    @Query(value = "UPDATE players " +
            "SET status = :status, fight = :fight, round_action_end = :roundActionEnd, round_change_ability = :roundChangeAbility, round_target_type = :roundTargetType, round_target_id = :roundTargetId " +
            "WHERE id = :id", nativeQuery = true)
    void clearPlayerFight(@Param("status") String status,
                          @Param("fight") Long fight,
                          @Param("roundActionEnd") boolean roundActionAnd,
                          @Param("roundChangeAbility") Long roundChangeAbility,
                          @Param("roundTargetType") String roundTargetType,
                          @Param("roundTargetId") Long roundTargetId,
                          @Param("id") Long id);

    //сохраняет примененное умение в бою и цель, по которой умение применено
    @Modifying
    @Query(value = "UPDATE players " +
            "SET round_action_end = :roundActionEnd, round_change_ability = :roundChangeAbility, round_target_type = :roundTargetType, round_target_id = :roundTargetId" +
            "WHERE id = :id", nativeQuery = true)
    void saveNewPlayerAttack(@Param("roundActionEnd") boolean roundActionAnd,
                             @Param("roundChangeAbility") Long roundChangeAbility,
                             @Param("roundTargetType") String roundTargetType,
                             @Param("roundTargetId") Long roundTargetId,
                             @Param("id") Long id);
}
