package com.cristhian.SistemaInventario.ServicioImplement;


import com.cristhian.SistemaInventario.DTO.CiudadDTO;
import com.cristhian.SistemaInventario.Excepciones.DuplicadoException;
import com.cristhian.SistemaInventario.Excepciones.RecursoNoEncontradoException;
import com.cristhian.SistemaInventario.Modelo.Ciudad;
import com.cristhian.SistemaInventario.Repositorio.CiudadRepository;
import com.cristhian.SistemaInventario.Service.ICiudadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CiudadServiceImpl implements ICiudadService {

    private static final Logger logger = LoggerFactory.getLogger(CiudadServiceImpl.class);
    @Autowired
    private CiudadRepository ciudadRepository;

    // -----------------------------------------------------------
    // 1. CONSULTAMOS TODOS LOS REGISTROS QUE ESTEN ACTIVOS
    // -----------------------------------------------------------
    @Override
    public List<Ciudad> listarCiudadesActivas(){
        return  ciudadRepository.findByActivoTrue();
    }

    // -----------------------------------------------------------
    // 2. CONSULTAMOS LOS REGISTROS POR ID
    // -----------------------------------------------------------
    @Override
    public Optional<Ciudad> buscarCiudadId(int id){
        return ciudadRepository.findById(id);
    }


    // -----------------------------------------------------------
    // 3. VALIDAMOS SI EXISTE LA CIUDAD Y SI ESTA ACTIVA, SI NO  CREAMOS LA CIUDAD
    // -----------------------------------------------------------
    @Override
    public Ciudad guardarCiudad(CiudadDTO ciudadDTO) {

        logger.info("JSON recibido → ciudad: {}, activo: {}",
                ciudadDTO.getCiudad(), ciudadDTO.isActivo());

        Optional<Ciudad> ciudadExistente = ciudadRepository.findByCiudadIgnoreCase(ciudadDTO.getCiudad().trim());

        // Si existe la ciudad
        if (ciudadExistente.isPresent()) {
            Ciudad ciudad = ciudadExistente.get();

            // Si existe pero está inactiva → Reactivar
            if (!ciudad.isActivo()) {
                logger.info("Ciudad encontrada inactiva. Se activará nuevamente.");
                ciudad.setActivo(true);
                return ciudadRepository.save(ciudad);   // ✔ IMPORTANTE: retornar
            }

            // Si ya existe y está activa → Error
            logger.info("La ciudad ya existe y está activa.");
            throw new DuplicadoException("Ya existe una ciudad con ese nombre");
        }

        // Si NO existe → Crear nueva
        logger.info("Ciudad nueva. Se creará una nueva entrada.");
        Ciudad ciudad = new Ciudad(ciudadDTO);
        return ciudadRepository.save(ciudad);
    }
    // -----------------------------------------------------------
    // 3. ACTUALIZAMOS LA CIUDAD
    // -----------------------------------------------------------
    public Ciudad actualizarCiudad(int id, CiudadDTO ciudadDTO) {

        // Validar si existe la ciudad con ese ID
        Ciudad ciudadExistente = ciudadRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("La ciudad no existe"));

        // Verificar si hay otra ciudad con el mismo nombre
        Optional<Ciudad> ciudadConMismoNombre =
                ciudadRepository.findByCiudadIgnoreCase(ciudadDTO.getCiudad().trim());

        if (ciudadConMismoNombre.isPresent()
                && ciudadConMismoNombre.get().getIdCiudad() != id) {
            throw new DuplicadoException("Ya existe otra ciudad con ese nombre");
        }

        // Actualizar datos
        ciudadExistente.setCiudad(ciudadDTO.getCiudad());
        ciudadExistente.setActivo(ciudadDTO.isActivo());

        return ciudadRepository.save(ciudadExistente);
    }


    // -----------------------------------------------------------
    // 5. INACTIVAMOS LA CIUDAD = "ES COMO SI SE FUERA A ELMINAR PERO EL REGISTRO QUEDA EN ESTADO FALSO"
    // -----------------------------------------------------------
    @Override
    public void eliminarCiudad(int id){
        Ciudad ciudad = ciudadRepository.findById(id).orElse(null);
        if (ciudad != null) {
            ciudad.setActivo(false); // solo la marcamos como inactiva
            ciudadRepository.save(ciudad);
        }
    }

    @Override
    public boolean existByCiudad(String ciudad){
        boolean existe = ciudadRepository.existsByCiudadIgnoreCase(ciudad);
        System.out.println("🔎 Buscando ciudad: " + ciudad + " → existe=" + existe);
        return existe;
    }
}
