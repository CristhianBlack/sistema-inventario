package com.cristhian.SistemaInventario.ServicioImplement;

import com.cristhian.SistemaInventario.DTO.ImpuestoDTO;
import com.cristhian.SistemaInventario.Enums.TipoImpuesto;
import com.cristhian.SistemaInventario.Excepciones.DuplicadoException;
import com.cristhian.SistemaInventario.Excepciones.RecursoNoEncontradoException;
import com.cristhian.SistemaInventario.Modelo.*;
import com.cristhian.SistemaInventario.Repositorio.ImpuestoRepository;
import com.cristhian.SistemaInventario.Service.IImpuestoService;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Servicio encargado de la gestión de Impuestos.
 *
 * Contiene la lógica para inicialización automática,
 * consulta, búsqueda e inactivación de impuestos.
 */
@Service
@Transactional
public class ImpuestoServiceImpl implements IImpuestoService {

    /**
     * Repositorio de impuestos.
     */
    private final ImpuestoRepository impuestoRepository;

    /**
     * Inyección por constructor del repositorio.
     *
     * @param impuestoRepository repositorio de impuestos
     */
    public ImpuestoServiceImpl(ImpuestoRepository impuestoRepository) {
        this.impuestoRepository = impuestoRepository;
    }

    /**
     * Inicializa automáticamente los impuestos permitidos
     * al arrancar la aplicación.
     *
     * Se ejecuta una sola vez al iniciar el contexto de Spring.
     */
    @PostConstruct
    public void inicializarImpuestos() {
        guardarImpuestoAutomatico();
    }

    /**
     * Obtiene el listado de impuestos activos.
     *
     * @return lista de impuestos activos
     */
    @Override
    public List<Impuesto> listarImpuestosActivos() {
        return impuestoRepository.findByActivoTrue();
    }

    /**
     * Busca un impuesto por su identificador.
     *
     * @param id identificador del impuesto
     * @return Optional con el impuesto si existe
     */
    @Override
    public Optional<Impuesto> buscarImpuestoPorId(int id) {
        return impuestoRepository.findById(id);
    }

    /**
     * Crea automáticamente los impuestos permitidos
     * si no existen en la base de datos.
     *
     * Evita duplicados y asigna el porcentaje
     * según el tipo de impuesto.
     */
    @Override
    public void guardarImpuestoAutomatico() {

        List<TipoImpuesto> impuestosPermitidos = List.of(
                TipoImpuesto.IVA,
                TipoImpuesto.EXENTO
        );

        for (TipoImpuesto tipo : impuestosPermitidos) {

            // Verifica si el impuesto ya existe
            if (!impuestoRepository.existsByTipoImpuesto(tipo)) {

                Impuesto impuesto = new Impuesto();
                impuesto.setTipoImpuesto(tipo);

                // Configuración automática según el tipo de impuesto
                switch (tipo) {

                    case IVA -> {
                        impuesto.setPorcentaje(
                                BigDecimal.valueOf(0.19)
                        );
                    }

                    case EXENTO -> {
                        impuesto.setPorcentaje(
                                BigDecimal.valueOf(0.00)
                        );
                    }
                }

                // Se marca como activo y se persiste
                impuesto.setActivo(true);
                impuestoRepository.save(impuesto);
            }
        }
    }

    /**
     * Inactiva un impuesto.
     *
     * No se elimina físicamente el registro,
     * solo se marca como inactivo.
     *
     * @param id identificador del impuesto
     */
    @Override
    public void borrarImpuesto(int id) {
        Impuesto impuesto = impuestoRepository.findById(id).orElse(null);
        if (impuesto != null) {
            impuesto.setActivo(false); // solo la marcamos como inactiva
            impuestoRepository.save(impuesto);
        }
    }
}

