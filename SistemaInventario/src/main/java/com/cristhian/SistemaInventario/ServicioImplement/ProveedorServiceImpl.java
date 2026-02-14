package com.cristhian.SistemaInventario.ServicioImplement;

import com.cristhian.SistemaInventario.DTO.ProveedorDTO;
import com.cristhian.SistemaInventario.DTO.ProveedorPersonaDTO;
import com.cristhian.SistemaInventario.Excepciones.DuplicadoException;
import com.cristhian.SistemaInventario.Excepciones.RecursoNoEncontradoException;
import com.cristhian.SistemaInventario.Excepciones.ValidacionException;
import com.cristhian.SistemaInventario.Mensaje.Mensaje;
import com.cristhian.SistemaInventario.Modelo.Ciudad;
import com.cristhian.SistemaInventario.Modelo.Persona;
import com.cristhian.SistemaInventario.Modelo.Proveedor;
import com.cristhian.SistemaInventario.Modelo.TipoDocumento;
import com.cristhian.SistemaInventario.Repositorio.PersonaRepository;
import com.cristhian.SistemaInventario.Repositorio.PersonaRolRepository;
import com.cristhian.SistemaInventario.Repositorio.ProveedorRepository;
import com.cristhian.SistemaInventario.Service.IProveedorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Optional;
/**
 * Servicio encargado de la gestión de proveedores.
 * Maneja creación, actualización, eliminación lógica
 * y consultas relacionadas con proveedor-persona.
 */
@Service
@Transactional
public class ProveedorServiceImpl implements IProveedorService {
    @Autowired
    private ProveedorRepository proveedorRepository;

    @Autowired
    private PersonaRepository personaRepository;

    @Autowired
    private PersonaRolRepository personaRolRepository;

    /**
     * Listar todos los proveedores activos.
     */
    @Override
    public List<Proveedor> listarProveedoresActivos(){
        return proveedorRepository.findByActivoTrue();
    }

    /**
     * Buscar proveedor por ID.
     */
    @Override
    public Optional<Proveedor> buscarProveedorId(int id){
        return proveedorRepository.findById(id);
    }

    /**
     * Crear un proveedor asociado a una persona existente.
     * Si el proveedor ya existe y está inactivo, se reactiva.
     */
    @Override
    public Proveedor guardarProveedor(ProveedorDTO proveedorDTO){

        Integer idPersona = proveedorDTO.getIdPersona();

        // Validar que la persona exista
        Persona persona = personaRepository.findById(idPersona)
                .orElseThrow(() -> new RecursoNoEncontradoException("La persona no existe"));

        // Validar si ya existe proveedor asociado a la persona
        Optional<Proveedor> proveedorExistente =
                proveedorRepository.findByPersonaIdPersona(idPersona);

        if (proveedorExistente.isPresent()) {
            Proveedor proveedor1 = proveedorExistente.get();

            // Si existe pero está inactivo → reactivar
            if (!proveedor1.isActivo()) {
                System.out.println("Proveedor encontrado inactivo. Se activará nuevamente.");
                proveedor1.setActivo(true);
                return proveedorRepository.save(proveedor1);
            }

            // Si existe y está activo → error
            throw new DuplicadoException("Ya existe ese proveedor.");
        }

        // Crear y guardar proveedor
        Proveedor proveedor = new Proveedor(proveedorDTO);
        proveedor.setPersona(persona);

        return proveedorRepository.save(proveedor);
    }

    /**
     * Actualizar los datos de un proveedor existente.
     */
    @Override
    public Proveedor actualizarProveedor(int id, ProveedorDTO proveedorDTO){

        // Validar que el proveedor exista
        Proveedor proveedor = proveedorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("La persona no existe."));

        // Obtener la persona asociada
        Persona persona = personaRepository.findById(proveedorDTO.getIdPersona())
                .orElseThrow(() -> new RuntimeException("Tipo de documento no encontrado"));

        // Actualizar datos
        proveedor.setDescripcionProveedor(proveedorDTO.getDescripcionProveedor());
        proveedor.setPersona(persona);

        return proveedorRepository.save(proveedor);
    }

    /**
     * Eliminación lógica del proveedor.
     */
    @Override
    public void borrarProveedor(int id){
        Proveedor proveedor = proveedorRepository.findById(id).orElse(null);
        if (proveedor != null){
            proveedor.setActivo(false);
            proveedorRepository.save(proveedor);
        }
    }

    /**
     * Obtener listado combinado de proveedor con datos de persona.
     */
    public List<ProveedorPersonaDTO> obtenerListadoProveedorPersona() {
        return proveedorRepository.listarProveedorPersona();
    }

}
