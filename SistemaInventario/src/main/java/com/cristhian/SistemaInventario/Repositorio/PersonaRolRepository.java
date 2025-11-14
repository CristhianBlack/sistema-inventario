package com.cristhian.SistemaInventario.Repositorio;

import com.cristhian.SistemaInventario.Modelo.PersonaRol;
import com.cristhian.SistemaInventario.Modelo.RolPersona;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PersonaRolRepository extends JpaRepository<PersonaRol, Integer> {

    // Traer los roles (PersonaRol) de una persona por su id
    List<PersonaRol> findByPersona_IdPersona(Integer idPersona);

    @Query("SELECT pr.rolPersona FROM PersonaRol pr WHERE pr.persona.idPersona = :idPersona AND pr.activo = true")
    List<RolPersona> findRolesByPersonaId(@Param("idPersona") Integer idPersona);

    @Query("SELECT pr FROM PersonaRol pr WHERE pr.persona.idPersona = :idPersona")
    List<PersonaRol> findByPersonaId(@Param("idPersona") Integer idPersona);
}
