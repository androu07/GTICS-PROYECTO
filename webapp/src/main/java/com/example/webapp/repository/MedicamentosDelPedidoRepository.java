package com.example.webapp.repository;

import com.example.webapp.entity.MedicamentosDelPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface MedicamentosDelPedidoRepository extends JpaRepository<MedicamentosDelPedido, Integer>{
    @Query(value = "SELECT * \n" +
            "FROM gticsbd.medicamentos_del_pedido \n" +
            "WHERE pedidos_paciente_idpedidos_paciente = ?1", nativeQuery = true)
    List<MedicamentosDelPedido> listaMedicamentosDely(int idpedido);

    @Transactional
    @Modifying
    @Query(value = "delete from gticsbd.medicamentos_del_pedido where nombre_medicamento = ?1 and pedidos_paciente_idpedidos_paciente = ?2 and pedidos_paciente_usuario_id_usuario = ?3", nativeQuery = true)
    void borrarMedicamentoPocoStock(String nombremedicamento, int idPedido, int usuid);
}
