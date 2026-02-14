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

    // Logger para trazabilidad de operaciones sobre ciudades
    private static final Logger logger = LoggerFactory.getLogger(CiudadServiceImpl.class);

    // Repositorio para la gestión de ciudades
    @Autowired
    private CiudadRepository ciudadRepository;

    /**
     * Obtiene todas las ciudades activas.
     *
     * @return lista de ciudades con activo = true
     */
    @Override
    public List<Ciudad> listarCiudadesActivas(){
        return ciudadRepository.findByActivoTrue();
    }

    /**
     * Busca una ciudad por su identificador.
     *
     * @param id identificador de la ciudad
     * @return Optional con la ciudad encontrada
     */
    @Override
    public Optional<Ciudad> buscarCiudadId(int id){
        return ciudadRepository.findById(id);
    }

    /**
     * Registra una ciudad nueva o reactiva una existente.
     *
     * Reglas de negocio:
     * - Si la ciudad existe y está inactiva, se reactiva
     * - Si la ciudad existe y está activa, se lanza error
     * - Si no existe, se crea una nueva ciudad
     *
     * @param ciudadDTO datos de la ciudad
     * @return ciudad creada o reactivada
     * @throws DuplicadoException si la ciudad ya existe y está activa
     */
    @Override
    public Ciudad guardarCiudad(CiudadDTO ciudadDTO) {

        logger.info("JSON recibido → ciudad: {}, activo: {}",
                ciudadDTO.getCiudad(), ciudadDTO.isActivo());

        // Búsqueda de ciudad por nombre (ignorando mayúsculas/minúsculas)
        Optional<Ciudad> ciudadExistente =
                ciudadRepository.findByCiudadIgnoreCase(ciudadDTO.getCiudad().trim());

        // Si la ciudad existe
        if (ciudadExistente.isPresent()) {
            Ciudad ciudad = ciudadExistente.get();

            // Si existe pero está inactiva → se reactiva
            if (!ciudad.isActivo()) {
                logger.info("Ciudad encontrada inactiva. Se activará nuevamente.");
                ciudad.setActivo(true);
                return ciudadRepository.save(ciudad);   // importante retornar
            }

            // Si ya existe y está activa → error
            logger.info("La ciudad ya existe y está activa.");
            throw new DuplicadoException("Ya existe una ciudad con ese nombre");
        }

        // Si no existe → se crea una nueva ciudad
        logger.info("Ciudad nueva. Se creará una nueva entrada.");
        Ciudad ciudad = new Ciudad(ciudadDTO);
        return ciudadRepository.save(ciudad);
    }

    /**
     * Actualiza los datos de una ciudad existente.
     *
     * Reglas de negocio:
     * - La ciudad debe existir
     * - No se permite duplicar el nombre con otra ciudad diferente
     *
     * @param id identificador de la ciudad
     * @param ciudadDTO datos actualizados
     * @return ciudad actualizada
     * @throws RecursoNoEncontradoException si la ciudad no existe
     * @throws DuplicadoException si el nombre ya está en uso por otra ciudad
     */
    @Override
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

        // Actualización de campos
        ciudadExistente.setCiudad(ciudadDTO.getCiudad());
        ciudadExistente.setActivo(ciudadDTO.isActivo());

        return ciudadRepository.save(ciudadExistente);
    }

    /**
     * Elimina lógicamente una ciudad.
     *
     * La eliminación se realiza marcando el registro como inactivo.
     *
     * @param id identificador de la ciudad
     */
    @Override
    public void eliminarCiudad(int id){
        Ciudad ciudad = ciudadRepository.findById(id).orElse(null);
        if (ciudad != null) {
            ciudad.setActivo(false); // eliminación lógica
            ciudadRepository.save(ciudad);
        }
    }

    /**
     * Verifica si existe una ciudad por su nombre.
     *
     * @param ciudad nombre de la ciudad
     * @return true si existe, false si no
     */
    @Override
    public boolean existByCiudad(String ciudad){
        boolean existe = ciudadRepository.existsByCiudadIgnoreCase(ciudad);
        System.out.println("🔎 Buscando ciudad: " + ciudad + " → existe=" + existe);
        return existe;
    }
}
