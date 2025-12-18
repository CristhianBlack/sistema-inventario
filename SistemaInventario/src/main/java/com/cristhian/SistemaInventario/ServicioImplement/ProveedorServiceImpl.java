package com.cristhian.SistemaInventario.ServicioImplement;

import com.cristhian.SistemaInventario.DTO.ProveedorDTO;
import com.cristhian.SistemaInventario.DTO.ProveedorPersonaDTO;
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

@Service
@Transactional
public class ProveedorServiceImpl implements IProveedorService {

    @Autowired
    private ProveedorRepository proveedorRepository;
    @Autowired
    private PersonaRepository personaRepository;
    @Autowired
    private PersonaRolRepository personaRolRepository;


    @Override
    public List<Proveedor> listarProveedoresActivos(){
        return proveedorRepository.findByActivoTrue();
    }

    @Override
    public Optional<Proveedor> buscarProveedorId(int id){
        return proveedorRepository.findById(id);
    }

    @Override
    public Proveedor guardarProveedor(ProveedorDTO proveedorDTO){

            Integer idPersona = proveedorDTO.getIdPersona();
            Integer ROL_PROVEEDOR = 3; // Cambia según tu BD

            //Validar existencia en persona
            Persona persona = personaRepository.findById(idPersona)
                    .orElseThrow(() -> new RecursoNoEncontradoException("La persona no existe"));



            // Guardar proveedor
            Proveedor proveedor = new Proveedor(proveedorDTO);
            proveedor.setPersona(persona);
            return proveedorRepository.save(proveedor);
        }

        @Override
    public Proveedor actualizarProveedor( int id, ProveedorDTO proveedorDTO){

        Proveedor proveedor = proveedorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("La persona no existe."));

        //Obtener entidades relacionadas
        Persona persona = personaRepository.findById(proveedorDTO.getIdPersona())
                .orElseThrow(() -> new RuntimeException("Tipo de documento no encontrado"));


        // Datos
        proveedor.setDescripcionProveedor(proveedorDTO.getDescripcionProveedor());
        proveedor.setPersona(persona);
        //proveedor.setFechaCreacion(proveedorDTO.getFechaCreacion());

        return proveedorRepository.save(proveedor);
    }

        @Override
    public void borrarProveedor(int id){
        Proveedor proveedor = proveedorRepository.findById(id).orElse(null);
        if (proveedor != null){
            proveedor.setActivo(false);
            proveedorRepository.save(proveedor);
        }
    }

    public List<ProveedorPersonaDTO> obtenerListadoProveedorPersona() {
        return proveedorRepository.listarProveedorPersona();
    }

}
