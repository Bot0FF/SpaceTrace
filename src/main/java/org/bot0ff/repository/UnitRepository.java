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

    //удаляет id сражения
    @Modifying
    @Query(value = "UPDATE unit SET " +
            "status = :status " +
            "WHERE id = :id", nativeQuery = true)
    void setStatus(@Param("status") String status,
                   @Param("id") Long id);
}
