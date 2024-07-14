package com.example.webapp.repository;

import com.example.webapp.entity.Carrito;
import com.example.webapp.entity.Tarjeta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.List;

@Repository
public interface TarjetaRepository extends JpaRepository<Tarjeta, Integer> {

    @Query(value = "SELECT * FROM gticsbd.tarjetas\n" +
            "WHERE numero = ?1 AND mes_caduca = ?2 AND anhio_caduca = ?3 AND cvv = ?4", nativeQuery = true)
    Tarjeta validarTarjeta(BigInteger numero, String mes, String anhio, Integer cvv);

    @Transactional
    @Modifying
    @Query(value = "delete from carrito where (medicamentos_id_medicamentos = ?1) and (`usuario_id_usuario` = ?2);", nativeQuery = true)
    void borrarElementoCarrito(int id, int usuid);

}