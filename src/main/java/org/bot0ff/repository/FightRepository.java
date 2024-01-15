package org.bot0ff.repository;

import org.bot0ff.entity.Fight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FightRepository extends JpaRepository<Fight, Long> {

    //сохраняет новый раунд
    @Modifying
    @Query(value = "UPDATE fight SET " +
            "count_round = :countRound, " +
            "result_round = :resultRound " +
            "WHERE id = :id", nativeQuery = true)
    void setNewRound(@Param("countRound") int countRound,
                     @Param("resultRound") String resultRound,
                     @Param("id") Long id);

    //сохраняет текстовый результат раунда
    @Modifying
    @Query(value = "UPDATE fight SET " +
            "result_round = :resultRound " +
            "WHERE id = :id", nativeQuery = true)
    void setTextResultRound(@Param("resultRound") String resultRound,
                     @Param("id") Long id);

    //устанавливает результат боя в бд
    @Modifying
    @Query(value = "UPDATE fight SET " +
            "fight_end = :fightEnd " +
            "WHERE id = :id", nativeQuery = true)
    void setStatusFight(@Param("fightEnd") boolean fightEnd,
                        @Param("id") Long id);
}
