package com.example.webapp.repository;


import com.example.webapp.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    public Usuario findByCorreo(String email);

    @Query(value = "SELECT dni FROM gticsbd.usuario;", nativeQuery = true)
    List<Integer> listaDniExistentes();

    @Query(value = "SELECT correo FROM gticsbd.usuario;", nativeQuery = true)
    List<String> listaCorreosExistentes();

    @Query(value = "select * from usuario where id_roles = ?1 and borrado_logico = ?2 and estado_solicitud = ?3 and cuenta_activada = 1 order by fecha_creacion", nativeQuery = true)
    List<Usuario> buscarFarmacistaAceptado(int rol,int borrado_logico,String estado_solicitud);

    @Query(value = "select * from usuario where id_roles = ?1 and borrado_logico = ?2 order by fecha_creacion", nativeQuery = true)
    List<Usuario> buscarFarmacista(int rol,int borrado_logico);

    @Query(value = "SELECT id_usuario FROM usuario ORDER BY id_usuario DESC LIMIT 1;\n", nativeQuery = true)
    int buscarUltimo();

    @Query(value = "select * from usuario where id_roles = ?1 and borrado_logico = ?2 ", nativeQuery = true)
    List<Usuario> buscarDoctor(int rol,int borrado_logico);

    @Query(value = "select * from usuario where id_roles = ?1 and borrado_logico = ?2 and cuenta_activada = 1 ", nativeQuery = true)
    List<Usuario> buscarAdministrador(int rol,int borrado_logico);

    @Query(value = "select * from usuario where id_roles = ?1 and borrado_logico = ?2 and cuenta_activada = 1 ", nativeQuery = true)
    List<Usuario> buscarPaciente(int rol,int borrado_logico);

    @Transactional
    @Modifying
    @Query(nativeQuery = true,value = "update usuario set borrado_logico = ?1 where id_usuario = ?2")
    void borradoLogico(int valor, int id);

    @Transactional
    @Modifying
    @Query(nativeQuery = true,value = "update usuario set estado = ?1 where id_usuario = ?2")
    void pasarActivo(int valor, int id);

    @Transactional
    @Modifying
    @Query(nativeQuery = true,value = "update usuario set estado = ?1 where id_usuario = ?2")
    void pasarInactivo(int valor, int id);

    @Transactional
    @Modifying
    @Query(nativeQuery = true,value = "update usuario set estado_solicitud = ?1 , estado = 1, borrado_logico = 0 where id_usuario = ?2")
    void aceptarAdministrador(String valor, int id);

    @Transactional
    @Modifying
    @Query(nativeQuery = true,value = "update usuario set estado_solicitud = ?1, estado = 0, cuenta_activada = 0, borrado_logico = 0, motivo_rechazo = ?2 where id_usuario = ?3")
    void rechazarAdministrador(String valor, String motivo_rechazo, int id);

    @Query(value = "select * from usuario where rol = ?1 and borrado_logico = ?2 order by fecha_creacion", nativeQuery = true)
    Usuario buscarSuperadmin(String rol,int borrado_logico);

    @Query(value = "select * from usuario u inner join usuario_has_sede uhs on (u.id_usuario = uhs.usuario_id_usuario) where id_roles = 3 and sede_id_sede = ?1", nativeQuery = true)
    List<Usuario> buscarFarmacistaporSede(int sede_id_sede);

    @Query(value = "select * from usuario u inner join usuario_has_sede uhs on (u.id_usuario = uhs.usuario_id_usuario) where id_roles = 5 and sede_id_sede = ?1", nativeQuery = true)
    List<Usuario> buscarDoctorporSede(int sede_id_sede);

    @Query(value = "select * from usuario where correo = ?1 ", nativeQuery = true)
    List<Usuario> buscarPorCorreo(String correo);

    @Query(value = "select * from usuario where token_recuperacion = ?1 ", nativeQuery = true)
    Usuario buscarPorToken(String token);

    @Transactional
    @Modifying
    @Query(value = "UPDATE usuario SET fecha_recuperacion = DATE_ADD(NOW(), INTERVAL 2 MINUTE), token_recuperacion = ?1 WHERE id_usuario = ?2", nativeQuery = true)
    int actualizarFechaYTokenRecuperacion(String token, int idUsuario);

    @Query(value = "CALL SP_Validar_Token(?1, ?2)", nativeQuery = true)
    String validarToken(int idUsuario, String tokenEnviado);

    @Transactional
    @Modifying
    @Query(value = "UPDATE usuario SET contrasena = ?1 , punto = ?2, token_recuperacion = null, fecha_recuperacion=null , cuenta_activada=1  "
            + "  WHERE id_usuario = ?3", nativeQuery = true)
    int actualizarPassword(String contrasena, String contrasena1,int idUsuario);

    @Transactional
    @Modifying
    @Query(value = "UPDATE usuario SET contrasena = ?1 , punto = ?2,"
            + " token_recuperacion = null, fecha_recuperacion=null , cuenta_activada=1, estado=1  "
            + "  WHERE id_usuario = ?3", nativeQuery = true)
    int actualizarPasswordyEstado(String contrasena, String contrasena1, int idUsuario);

    @Transactional
    @Modifying
    @Query(nativeQuery = true,value = "update usuario set id_distrito = ?1 where id_usuario = ?2")
    void actualizarFarmacista(int id_distrito, int id_usuario);
}
