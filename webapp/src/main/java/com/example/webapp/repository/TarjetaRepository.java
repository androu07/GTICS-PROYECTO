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
import java.util.Optional;

@Repository
public interface TarjetaRepository extends JpaRepository<Tarjeta, Integer> {

    @Query(value = "SELECT * FROM gticsbd.tarjetas\n" +
            "WHERE numero = ?1 AND mes_caduca = ?2 AND anhio_caduca = ?3 AND cvv = ?4", nativeQuery = true)
    Tarjeta validarTarjeta(BigInteger numero, String mes, String anhio, Integer cvv);

    @Query(value = "SELECT * FROM gticsbd.tarjetas\n" +
            "WHERE numero=?1", nativeQuery = true)
    Optional<Tarjeta> tarjetaDelPago(BigInteger numero);

}