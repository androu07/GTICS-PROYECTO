package com.example.webapp.repository;


import com.example.webapp.entity.PedidosPaciente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PedidosPacienteRepository1 extends JpaRepository<PedidosPaciente, Integer> {

}
