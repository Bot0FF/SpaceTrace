package org.bot0ff.repository;

import org.bot0ff.entity.Unit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UnitRepository extends JpaRepository<Unit, Long> {
    Optional<Unit> findByName(String name);

    //сохраняет новую позицию
    @Modifying
    @Query(value = "UPDATE unit SET " +
            "x = :x, " +
            "y = :y, " +
            "location = :location " +
            "WHERE id = :id", nativeQuery = true)
    void saveNewPosition(@Param("x") int x,
                         @Param("y")int y,
                         @Param("location") Long location,
                         @Param("id") Long id);

    //сохраняет новое сражение и обнуляет характеристики сражения
    @Modifying
    @Query(value = "UPDATE unit SET " +
            "action_end = :actionEnd, " +
            "status = :status, " +
            "fight = :fight, " +
            "_team_type = :_teamType, " +
            "_damage = :_damage, " +
            "_attack_type = :_attackType, " +
            "_target_id = :_targetId " +
            "WHERE id = :id", nativeQuery = true)
    void saveNewFight(@Param("actionEnd") boolean actionEnd,
                      @Param("status") String status,
                      @Param("fight") Long fight,
                      @Param("_teamType") String _teamType,
                      @Param("_damage") Long _damage,
                      @Param("_attackType") String _attackType,
                      @Param("_targetId") Long _targetId,
                      @Param("id") Long id);

    //сбрасывает настройки сражения
    @Modifying
    @Query(value = "UPDATE unit SET " +
            "action_end = :actionEnd, " +
            "_team_type = :_teamType, " +
            "_damage = :_damage, " +
            "_attack_type = :_attackType, " +
            "_target_id = :_targetId " +
            "WHERE id = :id", nativeQuery = true)
    void clearFight(@Param("actionEnd") boolean actionEnd,
                    @Param("_teamType") String _teamType,
                    @Param("_damage") Long _damage,
                    @Param("_attackType") String _attackType,
                    @Param("_targetId") Long _targetId,
                    @Param("id") Long id);

    //удаляет сражение
    @Modifying
    @Query(value = "UPDATE unit SET " +
            "action_end = :actionEnd, " +
            "status = :status, " +
            "fight = :fight, " +
            "_team_type = :_teamType, " +
            "_damage = :_damage, " +
            "_attack_type = :_attackType, " +
            "_target_id = :_targetId " +
            "WHERE id = :id", nativeQuery = true)
    void deleteFight(@Param("actionEnd") boolean actionEnd,
                     @Param("status") String status,
                     @Param("fight") Long fight,
                     @Param("_teamType") String _teamType,
                     @Param("_damage") Long _damage,
                     @Param("_attackType") String _attackType,
                     @Param("_targetId") Long _targetId,
                     @Param("id") Long id);

    //сохраняет новую атаку
    @Modifying
    @Query(value = "UPDATE unit SET " +
            "action_end = :actionEnd, " +
            "_damage = :_damage, " +
            "_attack_type = :_attackType, " +
            "_target_id = :_targetId " +
            "WHERE id = :id", nativeQuery = true)
    void saveNewAttack(@Param("actionEnd") boolean actionEnd,
                       @Param("_damage") Long _damage,
                       @Param("_attackType") String _attackType,
                       @Param("_targetId") Long _targetId,
                       @Param("id") Long id);
}
