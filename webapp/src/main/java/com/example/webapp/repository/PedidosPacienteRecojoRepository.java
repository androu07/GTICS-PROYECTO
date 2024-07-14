package com.example.webapp.repository;

import com.example.webapp.entity.PedidosPacienteRecojo;
import com.example.webapp.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PedidosPacienteRecojoRepository extends JpaRepository<PedidosPacienteRecojo, Integer> {
    List<PedidosPacienteRecojo> findByUsuario(Usuario usuario);

    @Query(value = "SELECT numero_tracking\n" +
            "FROM gticsbd.pedidos_paciente_recojo", nativeQuery = true)
    List<String> numerosDePedidosReco();

    @Query(value = "SELECT * FROM gticsbd.pedidos_paciente_recojo\n" +
            "WHERE usuario_id_usuario = ?1 AND numero_tracking like ?2\n" +
            "order by idpedidos_paciente_recojo desc;", nativeQuery = true)
    List<PedidosPacienteRecojo> buscarPedidosReco(int usuid, String numtrack);
}
