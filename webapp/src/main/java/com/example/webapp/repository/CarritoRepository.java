package com.example.webapp.repository;

import com.example.webapp.entity.Carrito;
import com.example.webapp.entity.CarritoId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface CarritoRepository extends JpaRepository<Carrito, CarritoId> {

    @Query(value = "SELECT *\n" +
            "FROM gticsbd.carrito\n" +
            "WHERE estado_de_compra != 'Registrado' and usuario_id_usuario = ?1", nativeQuery = true)
    List<Carrito> listarCarrito(Integer usuid);

    @Query(value = "SELECT *\n" +
            "FROM gticsbd.carrito\n" +
            "WHERE estado_de_compra = 'Comprando' and usuario_id_usuario = ?1", nativeQuery = true)
    List<Carrito> carritoPorId(int id);

    @Query(value = "SELECT gticsbd.carrito.estado_de_compra\n" +
            "FROM gticsbd.usuario\n" +
            "JOIN gticsbd.carrito ON gticsbd.usuario.id_usuario = gticsbd.carrito.usuario_id_usuario\n" +
            "WHERE usuario.id_usuario = ?1", nativeQuery = true)
    List<String> estadosDeCompraPorUsuarioId(int id);

    @Query(value = "SELECT numero_pedido\n" +
            "FROM gticsbd.carrito", nativeQuery = true)
    List<String> numerosDePedidosCarrito();

    @Query(value = "select * from carrito where medicamentos_id_medicamentos=?1", nativeQuery = true)
    List<Carrito> buscarDuplicados(int id);

    @Query(value = "select cantidad\n" +
            "from gticsbd.carrito\n" +
            "where medicamentos_id_medicamentos = ?1", nativeQuery = true)
    int cantidadDelDuplicado(int id);

    @Transactional
    @Modifying
    @Query(value = "delete from carrito where (medicamentos_id_medicamentos = ?1) and (`usuario_id_usuario` = ?2);", nativeQuery = true)
    void borrarElementoCarrito(int id, int usuid);

    @Transactional
    @Modifying
    @Query(value = "delete from gticsbd.carrito where (gticsbd.carrito.usuario_id_usuario = ?1) and (gticsbd.carrito.estado_de_compra = ?2);", nativeQuery = true)
    void borrarPedidoRegistrado(int usuid, String estadocompra);

    @Transactional
    @Modifying
    @Query(nativeQuery = true,value = "INSERT INTO gticsbd.carrito (medicamentos_id_medicamentos, usuario_id_usuario, cantidad, numero_pedido, estado_de_compra)\n" +
            "VALUES(?1, ?2, ?3, ?4, ?5)")
    void AnadirAlCarrito(int idMedicamentos, int idUsuario, int cantidad, String numpedido, String estadocompra);


    @Query(value = "SELECT gticsbd.carrito.numero_pedido\n" +
            "FROM gticsbd.carrito\n" +
            "WHERE gticsbd.carrito.usuario_id_usuario = ?1", nativeQuery = true)
    List<String> numPedidoPorUsuarioId(int id);

    @Query(value = "SELECT *\n" +
                "FROM gticsbd.pedidos_paciente\n" +
                "WHERE estado_del_pedido = 'Registrando' AND usuario_id_usuario = ?", nativeQuery = true)
    List<Integer> idpedidoPorUsuIdDely(int id);

    @Query(value = "SELECT * FROM gticsbd.pedidos_paciente\n" +
            "WHERE estado_del_pedido = 'Por cancelar' AND usuario_id_usuario = ?\n" +
            "ORDER by idpedidos_paciente desc\n" +
            "LIMIT 1", nativeQuery = true)
    List<Integer> idpedidoPorUsuIdDelyMedicamentos(int id);


    @Query(value = "SELECT *\n" +
            "FROM gticsbd.pedidos_paciente_recojo\n" +
            "WHERE estado_del_pedido = 'Registrando' AND usuario_id_usuario = ?", nativeQuery = true)
    List<Integer> idpedidoPorUsuIdReco(int id);

    @Query(value = "SELECT * FROM gticsbd.pedidos_paciente_recojo\n" +
            "WHERE estado_del_pedido = 'Por cancelar' AND usuario_id_usuario = ?\n" +
            "ORDER by idpedidos_paciente_recojo desc\n" +
            "LIMIT 1", nativeQuery = true)
    List<Integer> idpedidoPorUsuIdRecoMedicamentos(int id);

    @Query(value = "SELECT * FROM gticsbd.pedidos_paciente_recojo\n" +
            "WHERE estado_del_pedido = 'Pagado' AND usuario_id_usuario = ?\n" +
            "ORDER by idpedidos_paciente_recojo desc\n" +
            "LIMIT 1", nativeQuery = true)
    List<Integer> idpedidoPorUsuIdRecoMedicamentos2(int id);

    @Transactional
    @Modifying
    @Query(value = "SELECT gticsbd.carrito.cantidad * gticsbd.medicamentos.precio_unidad AS total\n" +
            "FROM gticsbd.carrito\n" +
            "JOIN \n" +
            "    gticsbd.medicamentos ON gticsbd.carrito.medicamentos_id_medicamentos = gticsbd.medicamentos.id_medicamentos " +
            "WHERE gticsbd.carrito.usuario_id_usuario = ?1", nativeQuery = true)
    List<Double> CantidadxPrecioUnitario(Integer usuario);

    @Transactional
    @Modifying
    @Query(nativeQuery = true,value = "insert into gticsbd.pedidos_paciente (costo_total, tipo_de_pedido, validacion_del_pedido, estado_del_pedido, numero_tracking, usuario_id_usuario)\n" +
            "VALUES(?1, ?2, ?3, ?4, ?5, ?6)")
    void registrarPedidoDely(double costototal, String tipopedido, String validacionpedido, String estadopedido, String numpedido, int usuid);

    @Transactional
    @Modifying
    @Query(nativeQuery = true,value = "insert into gticsbd.pedidos_paciente_recojo (costo_total, tipo_de_pedido, validacion_del_pedido, estado_del_pedido, numero_tracking, usuario_id_usuario)\n" +
            "VALUES(?1, ?2, ?3, ?4, ?5, ?6)")
    void registrarPedidoReco(double costototal, String tipopedido, String validacionpedido, String estadopedido, String numpedido, int usuid);

    @Transactional
    @Modifying
    @Query(nativeQuery = true,value = "UPDATE gticsbd.pedidos_paciente \n" +
            "SET nombre_paciente = ?1, \n" +
            "    apellido_paciente = ?2, \n" +
            "    dni = ?3,\n" +
            "    telefono = ?4, \n" +
            "    seguro = ?5, \t\n" +
            "    medico_que_atiende = ?6, \n" +
            "    aviso_vencimiento = ?7, \t\n" +
            "    fecha_solicitud = ?8,\n" +
            "    direccion = ?9, \n" +
            "    distrito = ?10, \n" +
            "    hora_de_entrega = ?11,\n" +
            "    estado_del_pedido = ?12\n" +
            "WHERE estado_del_pedido = 'Registrando' AND usuario_id_usuario = ?13")
    void finalizarPedido1(String nombre, String apellido, int dni, int telefono, String seguro, String medico, String vencimiento, String fechasoli, String direccion, String distrito, String horaentrega, String estadopedido,int usuid);

    @Transactional
    @Modifying
    @Query(nativeQuery = true,value = "UPDATE gticsbd.pedidos_paciente_recojo \n" +
            "SET nombre_paciente = ?1, \n" +
            "    apellido_paciente = ?2, \n" +
            "    dni = ?3,\n" +
            "    telefono = ?4, \n" +
            "    seguro = ?5, \t\n" +
            "    medico_que_atiende = ?6, \n" +
            "    aviso_vencimiento = ?7, \t\n" +
            "    fecha_solicitud = ?8,\n" +
            "    estado_del_pedido = ?9,\n" +
            "    sede_de_recojo = ?10\n" +
            "WHERE estado_del_pedido = 'Registrando' AND usuario_id_usuario = ?11")
    void finalizarPedido2(String nombre, String apellido, int dni, int telefono, String seguro, String medico, String vencimiento, String fechasoli, String estadopedido, String sederecojo, int usuid);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM gticsbd.pedidos_paciente_recojo\n" +
            "WHERE usuario_id_usuario = ?1\n" +
            "AND estado_del_pedido = 'Registrando';", nativeQuery = true)
    void cancelarPedidoReco(int usuid);

    @Query(value = "SELECT idpedidos_paciente\n" +
            "FROM gticsbd.pedidos_paciente\n" +
            "WHERE estado_del_pedido = 'Registrando' AND usuario_id_usuario = ?", nativeQuery = true)
    Integer idPedidoRegistrando(int id);

    @Query(value = "SELECT idpedidos_paciente\n" +
            "FROM gticsbd.pedidos_paciente\n" +
            "WHERE tipo_de_pedido = 'Web - Delivery' AND estado_del_pedido = 'Por cancelar' AND usuario_id_usuario = ?", nativeQuery = true)
    List<Integer> listaPedidosPorCancelar1(int id);

    @Query(value = "SELECT idpedidos_paciente\n" +
            "FROM gticsbd.pedidos_paciente\n" +
            "WHERE tipo_de_pedido = 'Pre-orden' AND estado_del_pedido = 'Por cancelar' AND usuario_id_usuario = ?", nativeQuery = true)
    List<Integer> listaPedidosPorCancelar2(int id);

    @Query(value = "SELECT idpedidos_paciente_recojo    \n" +
            "FROM gticsbd.pedidos_paciente_recojo\n" +
            "WHERE estado_del_pedido = 'Por cancelar' AND usuario_id_usuario = ?", nativeQuery = true)
    List<Integer> listaPedidosPorCancelar3(int id);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM gticsbd.pedidos_paciente\n" +
            "WHERE usuario_id_usuario = ?1\n" +
            "AND estado_del_pedido = 'Registrando';", nativeQuery = true)
    void cancelarPedidoDely(int usuid);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM gticsbd.medicamentos_del_pedido \n" +
            "WHERE pedidos_paciente_usuario_id_usuario = ?1 \n" +
            "AND pedidos_paciente_idpedidos_paciente = ?2", nativeQuery = true)
    void borrarMedicamentosAlCancelar(int usuid, int idpedidos);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM gticsbd.carrito\n" +
            "WHERE usuario_id_usuario = ?1", nativeQuery = true)
    void borrarCarritoPorId(int usuid);

    @Query(value = "SELECT idpedidos_paciente\n" +
            "FROM gticsbd.pedidos_paciente\n" +
            "WHERE usuario_id_usuario = ?1\n" +
            "AND estado_del_pedido = 'Registrando';", nativeQuery = true)
    List<Integer> idpedidoRegistrandoPreorden(int usuid);

    @Transactional
    @Modifying
    @Query(value = "INSERT INTO gticsbd.medicamentos_del_pedido (nombre_medicamento, costo_medicamento, cantidad, pedidos_paciente_idpedidos_paciente, pedidos_paciente_usuario_id_usuario)\n" +
            "SELECT m.nombre AS nombre_medicamento, m.precio_unidad AS costo_medicamento, c.cantidad, ?1, ?2\n" +
            "FROM gticsbd.carrito c\n" +
            "JOIN gticsbd.medicamentos m ON c.medicamentos_id_medicamentos = m.id_medicamentos;", nativeQuery = true)
    void registrarMedicamentosPedidoDely(int idpedido, int usuid);

    @Transactional
    @Modifying
    @Query(value = "INSERT INTO gticsbd.medicamentos_del_pedido (id_medicamentos_del_pedido, nombre_medicamento, costo_medicamento, cantidad, pedidos_paciente_idpedidos_paciente, pedidos_paciente_usuario_id_usuario)" +
            " VALUES (?1, ?2, ?3, ?4, ?5, ?6);", nativeQuery = true)
    void registrarMedicamentosPedidoPreorden(int id, String nombre, double costo, int cantidad, int idpedido, int usuid);

    @Transactional
    @Modifying
    @Query(value = "UPDATE gticsbd.medicamentos_del_pedido\n" +
            "SET pedidos_paciente_idpedidos_paciente = ?1\n" +
            "WHERE pedidos_paciente_idpedidos_paciente = ?2\n" +
            "AND pedidos_paciente_usuario_id_usuario = ?3", nativeQuery = true)
    void registrarMedicamentosPedidoPreorden2(int idpedidodesp, int idpedidoant, int usuid);

    @Transactional
    @Modifying
    @Query(value = "INSERT INTO gticsbd.medicamentos_recojo (nombre_medicamento, costo_medicamento, cantidad, pedidos_paciente_recojo_idpedidos_paciente_recojo, pedidos_paciente_recojo_usuario_id_usuario)\n" +
            "SELECT m.nombre AS nombre_medicamento, m.precio_unidad AS costo_medicamento, c.cantidad, ?1, ?2\n" +
            "FROM gticsbd.carrito c\n" +
            "JOIN gticsbd.medicamentos m ON c.medicamentos_id_medicamentos = m.id_medicamentos;", nativeQuery = true)
    void registrarMedicamentosPedidoReco(int idpedido, int usuid);

    @Query(value = "SELECT precio_unidad\n" +
            "FROM gticsbd.medicamentos\n" +
            "WHERE id_medicamentos = ?1", nativeQuery = true)
    List<Double> precioDelMedicamento(int id);

    @Query(value = "SELECT nombre\n" +
            "FROM gticsbd.medicamentos\n" +
            "WHERE id_medicamentos = ?1", nativeQuery = true)
    List<String> nombreDelMedicamento(int id);



    //eliminar posteriormente
    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "UPDATE gticsbd.pedidos_paciente_recojo " +
            "SET nombre_paciente = ?1, " +
            "    apellido_paciente = ?2, " +
            "    dni = ?3, " +
            "    sede_de_recojo = ?4 " +
            "WHERE estado_del_pedido = 'Registrando' AND usuario_id_usuario = ?5")
    void finalizarPedido22(String nombre, String apellido, int dni, String sederecojo, int usuid);

    @Query(value = "SELECT *\n" +
            "FROM carrito\n" +
            "WHERE usuario_id_usuario = ?1 AND estado_de_compra != 'Registrado';", nativeQuery = true)
    List<Carrito> listarCarritoxUsuario(int usuid);

    @Query(value = "SELECT * FROM carrito WHERE numero_pedido = ?1", nativeQuery = true)
    List<Carrito> listadodelcarritorxNumPed(String numero_pedido);

    @Query(value = "SELECT gticsbd.carrito.cantidad * gticsbd.medicamentos.precio_unidad AS total FROM gticsbd.carrito JOIN \n" +
            "gticsbd.medicamentos ON gticsbd.carrito.medicamentos_id_medicamentos = gticsbd.medicamentos.id_medicamentos \n" +
            "WHERE numero_pedido = ?1", nativeQuery = true)
    List<Double> CantidadxPrecioUnitarioxNumPed(String numero_pedido);

    @Transactional
    @Modifying
    @Query(nativeQuery = true,value = "insert into gticsbd.pedidos_reposicion (usuario_id_usuario, fecha_solicitud, costo_total, fecha_entrega, estado_de_reposicion,proveedor_id_proveedor)\n" +
            "VALUES(?1, ?2, ?3, ?4, ?5, ?6)")
    void registrarPedidoRepo1(int usuid, String fechaSolicitud, double costoTotal, String fechaEntrega, String estadoReposcion, int idProovedor);


    @Transactional
    @Modifying

    @Query(nativeQuery = true,value = "insert into gticsbd.pedidos_reposicion_has_medicamentos (pedidos_reposicion_id_pedidos_reposicion, pedidos_reposicion_usuario_id_usuario, pedidos_reposicion_proveedor_id_proveedor,medicamentos_id_medicamentos,cantidad) \n" +
            "VALUES(?1, ?2, ?3, ?4, ?5)")
    void registrarPedidoRepo3(int idPedidosReposicion, int usid, int idProovedor,int idMedicamento, int cantidad);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM gticsbd.pedidos_reposicion\n" +
            "WHERE id_pedidos_reposicion = ?1\n" +
            "AND estado_de_reposicion = 'Solicitado';", nativeQuery = true)
    void eliminarPedidoRepo(int id);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM gticsbd.carrito\n" +
            "WHERE usuario_id_usuario = ?1\n" +
            "AND estado_de_compra = 'Comprando';", nativeQuery = true)
    void eliminarCarrito(int usuid);




}
