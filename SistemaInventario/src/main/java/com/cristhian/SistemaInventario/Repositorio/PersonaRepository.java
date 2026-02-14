package com.cristhian.SistemaInventario.Repositorio;

import com.cristhian.SistemaInventario.Enums.NombreRol;
import com.cristhian.SistemaInventario.Modelo.Persona;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PersonaRepository extends JpaRepository<Persona, Integer> {

    long count();
    Optional<Persona> existsByDocumentoPersona(String documentoPersona);

    List<Persona> findByActivoTrue();

    Optional<Persona> findByDocumentoPersona(String documentoPersona);




}
