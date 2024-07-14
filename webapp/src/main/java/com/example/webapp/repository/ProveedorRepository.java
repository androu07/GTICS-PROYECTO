package com.example.webapp.repository;

import com.example.webapp.entity.Distrito;
import com.example.webapp.entity.Proveedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProveedorRepository extends JpaRepository<Proveedor, Integer> {
}
