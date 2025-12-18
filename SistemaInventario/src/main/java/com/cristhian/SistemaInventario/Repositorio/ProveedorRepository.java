package com.cristhian.SistemaInventario.Repositorio;

import com.cristhian.SistemaInventario.DTO.ProveedorPersonaDTO;
import com.cristhian.SistemaInventario.Modelo.Proveedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProveedorRepository extends JpaRepository<Proveedor, Integer> {

    List<Proveedor> findByActivoTrue();

    @Query("""
    SELECT new com.cristhian.SistemaInventario.DTO.ProveedorPersonaDTO(
        p.idProveedor,
        p.descripcionProveedor,
        p.activo,
        per.idPersona,
        per.nombre,
        per.apellido,
        per.segundoApellido,
        per.documentoPersona,
        per.telefono,
        per.email,
        per.direccion
    )
    FROM Proveedor p
    JOIN p.persona per
    WHERE p.activo = true
""")
    List<ProveedorPersonaDTO> listarProveedorPersona();

}
