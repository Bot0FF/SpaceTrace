package org.bot0ff.repository;

import org.bot0ff.entity.Enemy;
import org.bot0ff.entity.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EnemyRepository extends JpaRepository<Enemy, Long> {
    Optional<Enemy> findByName(String name);

    @Modifying
    @Query(value = "UPDATE enemy SET status = :status, fight = :fight WHERE id = :id", nativeQuery = true)
    void saveNewEnemyFightId(@Param("status") String status, @Param("fight") Long fight, @Param("id") Long id);

    //сбрасывает настройки сражения enemy
    @Modifying
    @Query(value = "UPDATE enemy " +
            "SET status = :status, fight = :fight, round_action_end = :roundActionEnd, round_change_ability = :roundChangeAbility, round_target_type = :roundTargetType, round_target_id = :roundTargetId " +
            "WHERE id = :id", nativeQuery = true)
    void clearEnemyFight(@Param("status") String status,
                          @Param("fight") Long fight,
                          @Param("roundActionEnd") boolean roundActionEnd,
                          @Param("roundChangeAbility") Long roundChangeAbility,
                          @Param("roundTargetType") String roundTargetType,
                          @Param("roundTargetId") Long roundTargetId,
                          @Param("id") Long id);
}
