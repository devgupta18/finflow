package com.example.finflow.repository;

import com.example.finflow.entity.BatchJob;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BatchJobRepository extends JpaRepository<BatchJob, Long> {
}
