package com.cristhian.SistemaInventario.Repositorio;

import com.cristhian.SistemaInventario.DTO.PersonaDTO;
import com.cristhian.SistemaInventario.Modelo.Persona;
import com.cristhian.SistemaInventario.Modelo.PersonaRol;
import com.cristhian.SistemaInventario.Modelo.RolPersona;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PersonaRolRepository extends JpaRepository<PersonaRol, Integer> {

    // Traer los roles (PersonaRol) de una persona por su id
    List<PersonaRol> findByPersona(Persona persona);

    @Query("SELECT pr.rolPersona FROM PersonaRol pr WHERE pr.persona.idPersona = :idPersona AND pr.activo = true")
    List<RolPersona> findRolesByPersonaId(@Param("idPersona") Integer idPersona);

    @Query("SELECT pr.rolPersona FROM PersonaRol pr WHERE pr.persona.idPersona = :idPersona AND pr.activo = true")
    List<PersonaRol> findByPersonaId(@Param("idPersona") Integer idPersona);

    @Query("""
        SELECT pr
        FROM PersonaRol pr
        JOIN FETCH pr.rolPersona rp
        WHERE pr.persona.idPersona = :idPersona
        AND pr.activo = true
    """)
    List<PersonaRol> findRolesActivosByPersonaId(@Param("idPersona") Integer idPersona);


    boolean existsByPersonaIdPersonaAndRolPersonaIdRolPersona(Integer idPersona, Integer idRolPersona);

    @Query("SELECT pr.persona FROM PersonaRol pr WHERE pr.rolPersona.idRolPersona = :idRol AND pr.activo = true")
    List<Persona> findPersonasConRol(@Param("idRol") Integer idRol);

    // Obtener personas CON DTO rol puede ser proveedor o cliente (para mostrarlas en frontend)
    @Query("""
    SELECT new com.cristhian.SistemaInventario.DTO.PersonaDTO(
        p.idPersona,
        p.tipoPersona.idTipoPersona,
        p.razonSocial,
        p.nombre,
        p.apellido,
        p.segundoApellido,
        p.nombreContacto,
        p.apellidoContacto,
        p.segundoApellidoContacto,
        p.documentoPersona,
        p.telefono,
        p.email,
        p.direccion,
        p.saldoFavor
    )
    FROM PersonaRol pr
    JOIN pr.persona p
    WHERE pr.rolPersona.idRolPersona = :idRol
      AND pr.activo = true
""")
    List<PersonaDTO> findPersonasConRolDTO(@Param("idRol") Integer idRol);

    boolean existsByPersona_IdPersonaAndRolPersona_IdRolPersonaAndActivoTrue(
            Integer idPersona,
            Integer idRolPersona
    );


}