package com.example.webapp.repository;

import com.example.webapp.entity.MedicamentoRecojo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MedicamentosRecojoRepository extends JpaRepository<MedicamentoRecojo, Integer> {
    @Query(value = "SELECT * \n" +
            "FROM gticsbd.medicamentos_recojo \n" +
            "WHERE pedidos_paciente_recojo_idpedidos_paciente_recojo = ?1", nativeQuery = true)
    List<MedicamentoRecojo> listaMedicamentosReco(int idpedido);


}
