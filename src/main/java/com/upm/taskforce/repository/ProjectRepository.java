package com.upm.taskforce.repository;

import com.upm.taskforce.entity.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Data access for Project entity.
 * Inherits standard CRUD operations from JpaRepository.
 */
@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    @Query("SELECT p FROM Project p LEFT JOIN FETCH p.manager")
    List<Project> findAllWithDetails();

    @Query(value = "SELECT p FROM Project p LEFT JOIN FETCH p.manager",
           countQuery = "SELECT count(p) FROM Project p")
    Page<Project> findAllWithDetails(Pageable pageable);
}