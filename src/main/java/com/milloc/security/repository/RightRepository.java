package com.milloc.security.repository;

import com.milloc.security.entity.RightEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RightRepository extends JpaRepository<RightEntity, String> {
}
