package org.bot0ff.repository;

import org.bot0ff.entity.Thing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ThingRepository extends JpaRepository<Thing, Long> {
    List<Thing> findAllByOwnerId(Long ownerId);
}
