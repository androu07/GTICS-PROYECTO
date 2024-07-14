package com.example.webapp.repository;

import com.example.webapp.entity.Usuario;
import com.example.webapp.entity.UsuarioHasSede;
import com.example.webapp.entity.UsuarioHasSedeId;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UsuarioHasSedeRepository extends JpaRepository<UsuarioHasSede, UsuarioHasSedeId> {

    @Query(value = "select * from usuario_has_sede where usuario_id_usuario = ?1", nativeQuery = true)
    List<UsuarioHasSede> buscarSede(int id);

    @Transactional
    @Modifying
    @Query(nativeQuery = true,value = "delete from usuario_has_sede where usuario_id_usuario= ?1 and sede_id_sede=?2")
    void NoAsignarSede(int idUsuario, int idSede);

    @Transactional
    @Modifying
    @Query(nativeQuery = true,value = "INSERT INTO usuario_has_sede (usuario_id_usuario,sede_id_sede)\n" +
            "VALUES \n" +
            "(?1,?2)")
    void AsignarSede(int idUsuario, int idSede);

    @Transactional
    @Modifying
    @Query(nativeQuery = true,value = "delete from usuario_has_sede where usuario_id_usuario= ?1")
    void AsignarSedeBorrando(int idUsuario);

    @Query(value = "select sede_id_sede from usuario_has_sede where usuario_id_usuario = ?1", nativeQuery = true)
    int buscarSedeDeUsuario(int id);


}
