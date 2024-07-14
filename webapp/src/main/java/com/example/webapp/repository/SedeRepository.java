package com.example.webapp.repository;

import com.example.webapp.dto.MedicamentoSolicitados3mesesDto;
import com.example.webapp.dto.SedesConMayorCantTransaccionesDto;
import com.example.webapp.entity.Sede;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SedeRepository extends JpaRepository<Sede, Integer> {
    @Query(value = "SELECT s.nombre as Sede, count(s.nombre) as CantidadTransacciones FROM sede s inner join usuario_has_sede uhs on (s.id_sede = uhs.sede_id_sede) \n" +
            "inner join pedidos_reposicion_has_medicamentos prhm on (uhs.usuario_id_usuario = prhm.pedidos_reposicion_usuario_id_usuario) \n" +
            "group by s.nombre order by CantidadTransacciones desc limit 5;", nativeQuery = true)
    List<SedesConMayorCantTransaccionesDto> SedesConMayorCantTransacciones();
}
