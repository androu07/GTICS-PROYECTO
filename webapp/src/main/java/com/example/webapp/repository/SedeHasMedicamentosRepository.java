package com.example.webapp.repository;

import com.example.webapp.entity.SedeHasMedicamentos;
import com.example.webapp.entity.SedeHasMedicamentosId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface SedeHasMedicamentosRepository extends JpaRepository<SedeHasMedicamentos, SedeHasMedicamentosId> {
    @Transactional
    @Modifying
    @Query(nativeQuery = true,value = "delete from sede_has_medicamentos where sede_id_sede= ?1 and medicamentos_id_medicamentos=?2")
    void NoAsignarSedeMedicamento(int idSede, int idMedicamento);

    @Transactional
    @Modifying
    @Query(nativeQuery = true,value = "INSERT INTO sede_has_medicamentos (sede_id_sede,medicamentos_id_medicamentos)\n" +
            "VALUES \n" +
            "(?1,?2)")
    void AsignarSedeMedicamento(int idSede, int idMedicamento);
}
