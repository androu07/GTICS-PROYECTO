package com.example.webapp.repository;

import com.example.webapp.entity.CodigoColegio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CodigoColegioRepository extends JpaRepository<CodigoColegio, Integer> {
}
