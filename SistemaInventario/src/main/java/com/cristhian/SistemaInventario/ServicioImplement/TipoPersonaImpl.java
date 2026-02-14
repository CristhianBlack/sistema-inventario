package com.cristhian.SistemaInventario.ServicioImplement;


import com.cristhian.SistemaInventario.Enums.NombreTipoPersona;
import com.cristhian.SistemaInventario.Modelo.TipoPersona;
import com.cristhian.SistemaInventario.Repositorio.TipoPersonaRepository;
import com.cristhian.SistemaInventario.Service.ITipoPersonaService;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



import java.util.List;
import java.util.Optional;
/**
 * Servicio encargado de la gestión de los tipos de persona.
 * Controla la creación automática, listado, búsqueda y
 * desactivación lógica de los tipos de persona del sistema.
 */
@Service
@Transactional
public class TipoPersonaImpl implements ITipoPersonaService {

    private final TipoPersonaRepository tipoPersonaRepository;

    public TipoPersonaImpl(TipoPersonaRepository tipoPersonaRepository) {
        this.tipoPersonaRepository = tipoPersonaRepository;
    }

    /**
     * Inicializa los tipos de persona por defecto
     * al iniciar la aplicación.
     */
    @PostConstruct
    public void inicializarTipoPersona() {
        guardarTipoPersonaPorDefecto();
    }

    /**
     * Lista todos los tipos de persona activos.
     */
    @Override
    public List<TipoPersona> listarTipoPersonaActiva() {
        return tipoPersonaRepository.findByActivoTrue();
    }

    /**
     * Busca un tipo de persona por su ID.
     */
    @Override
    public Optional<TipoPersona> buscarTipoPersonaId(int id) {
        return tipoPersonaRepository.findById(id);
    }

    /**
     * Crea automáticamente los tipos de persona permitidos
     * cuando se ejecuta la aplicación.
     *
     * Esta lógica evita que el usuario cree tipos de persona
     * que no existen o no son válidos dentro del sistema.
     */
    @Override
    @Transactional
    public void guardarTipoPersonaPorDefecto() {

        List<NombreTipoPersona> nombresPermitidos = List.of(
                NombreTipoPersona.PERSONA_NATURAL,
                NombreTipoPersona.PERSONA_JURIDICA,
                NombreTipoPersona.OTRO
        );

        for (NombreTipoPersona tipoPersona : nombresPermitidos) {

            if (!tipoPersonaRepository.existsByNombreTipoPersona(tipoPersona)) {

                TipoPersona tipo = new TipoPersona();
                tipo.setNombreTipoPersona(tipoPersona);
                tipo.setActivo(true);

                tipoPersonaRepository.save(tipo);
            }
        }
    }

    /**
     * Desactiva un tipo de persona de forma lógica.
     * No elimina el registro de la base de datos.
     */
    @Override
    public void desactivarTipoDocumento(int id) {
        TipoPersona tipoPersona = tipoPersonaRepository.findById(id).orElse(null);
        if (tipoPersona != null) {
            tipoPersona.setActivo(false);
            tipoPersonaRepository.save(tipoPersona);
        }
    }
}
