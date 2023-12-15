package org.bot0ff.repository;

import org.bot0ff.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {

}

