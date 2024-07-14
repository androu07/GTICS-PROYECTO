package com.example.webapp.repository;


import com.example.webapp.entity.PedidosPaciente;
import com.example.webapp.entity.PedidosPacienteRecojo;
import com.example.webapp.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PedidosPacienteRepository extends JpaRepository<PedidosPaciente, Integer> {

    @Query(value = "select * from pedidos_paciente where tipo_de_pedido = ?1", nativeQuery = true)
    List<PedidosPaciente> buscarPedidosPorTipo(String tipo_de_pedido);

    @Query(value = "SELECT * \n" +
            "FROM gticsbd.pedidos_paciente \n" +
            "WHERE usuario_id_usuario = ?1 AND tipo_de_pedido = 'Web - Delivery'" +
            "order by idpedidos_paciente desc;", nativeQuery = true)
    List<PedidosPaciente> pedidosDelivery(int usuid);

    @Query(value = "SELECT * FROM gticsbd.pedidos_paciente\n" +
            "WHERE tipo_de_pedido = 'Web - Delivery' AND usuario_id_usuario = ?1 AND numero_tracking like ?2\n" +
            "order by idpedidos_paciente desc;", nativeQuery = true)
    List<PedidosPaciente> buscarPedidosDelivery(int usuid, String numtrack);

    @Query(value = "SELECT * \n" +
            "FROM gticsbd.pedidos_paciente \n" +
            "WHERE usuario_id_usuario = ?1 AND tipo_de_pedido = 'Pre-orden'" +
            "order by idpedidos_paciente desc;", nativeQuery = true)
    List<PedidosPaciente> pedidosPreorden(int usuid);

    @Query(value = "SELECT * \n" +
            "FROM gticsbd.pedidos_paciente \n" +
            "WHERE usuario_id_usuario = ?1 AND tipo_de_pedido = 'Pre-orden' AND numero_tracking like ?2" +
            "order by idpedidos_paciente desc;", nativeQuery = true)
    List<PedidosPaciente> buscarPedidosPreorden(int usuid, String numtrack);

    @Query(value = "SELECT * \n" +
            "FROM gticsbd.pedidos_paciente_recojo \n" +
            "WHERE usuario_id_usuario = ?1 AND estado_del_pedido != 'Registrando';", nativeQuery = true)
    List<String> pedidosRecojo(int usuid);

    @Query(value = "SELECT numero_tracking\n" +
            "FROM gticsbd.pedidos_paciente;", nativeQuery = true)
    List<String> numerosDePedidosDely();

}
