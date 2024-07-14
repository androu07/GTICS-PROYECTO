package com.example.webapp.repository;

import com.example.webapp.entity.Distrito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DistritoRepository extends JpaRepository<Distrito, Integer> {
}
