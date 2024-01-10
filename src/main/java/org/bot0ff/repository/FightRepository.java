package org.bot0ff.repository;

import org.bot0ff.entity.Fight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FightRepository extends JpaRepository<Fight, Long> {

    //устанавливает результат боя в бд
    @Modifying
    @Query(value = "UPDATE fight SET fight_end = :fightEnd WHERE id = :id", nativeQuery = true)
    void setStatusFight(@Param("fightEnd") boolean fightEnd, @Param("id") Long id);
}
