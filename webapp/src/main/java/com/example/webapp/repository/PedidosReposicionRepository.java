package com.example.webapp.repository;

import com.example.webapp.dto.MedicamentosPocoInventarioDto;
import com.example.webapp.entity.PedidosReposicion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface PedidosReposicionRepository extends JpaRepository<PedidosReposicion, Integer> {
    @Query(value = "SELECT * FROM pedidos_reposicion where usuario_id_usuario = ?1 order by fecha_solicitud desc", nativeQuery = true)
    List<PedidosReposicion> listarPedRepPorIdUsuario(int usuario_id_usuario);
    @Query(value = "SELECT id_pedidos_reposicion FROM gticsbd.pedidos_reposicion where fecha_solicitud =?1 and usuario_id_usuario =?2", nativeQuery = true)
    int idPedRexfecha(String fecha, int usdId);

    @Query(value = "SELECT * FROM gticsbd.pedidos_reposicion where fecha_solicitud =?1 and usuario_id_usuario =?2", nativeQuery = true)
    List<PedidosReposicion> PedRexfecha(String fecha, int usdId);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM gticsbd.pedidos_reposicion_has_medicamentos WHERE pedidos_reposicion_id_pedidos_reposicion = ?1", nativeQuery = true)
    void eliminarTodo(int id);

    @Transactional
    @Modifying
    @Query(nativeQuery = true,value = "UPDATE gticsbd.pedidos_reposicion \n" +
            "SET proveedor_id_proveedor = ?1\n" +
            "WHERE id_pedidos_reposicion = ?2 ")
    void actualizarProovedor(int idProve, int idPedRep);

}
