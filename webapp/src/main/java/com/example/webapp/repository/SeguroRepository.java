package com.example.webapp.repository;

import com.example.webapp.entity.Seguro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SeguroRepository extends JpaRepository<Seguro, Integer> {
}
