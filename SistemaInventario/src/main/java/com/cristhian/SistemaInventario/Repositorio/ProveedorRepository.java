package com.cristhian.SistemaInventario.Repositorio;

import com.cristhian.SistemaInventario.DTO.ProveedorPersonaDTO;
import com.cristhian.SistemaInventario.Modelo.Proveedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProveedorRepository extends JpaRepository<Proveedor, Integer> {

    long count();

    List<Proveedor> findByActivoTrue();
    boolean existsByPersonaIdPersona(Integer idPersona);

    Optional<Proveedor> findByPersonaIdPersona(Integer idPersona);

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
                    per.direccion,
                    per.razonSocial,
                    per.tipoPersona.idTipoPersona
                )
                FROM Proveedor p
                JOIN p.persona per
                WHERE p.activo = true
""")
    List<ProveedorPersonaDTO> listarProveedorPersona();

}
