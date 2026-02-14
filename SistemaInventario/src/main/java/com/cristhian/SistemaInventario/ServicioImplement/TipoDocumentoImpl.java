package com.cristhian.SistemaInventario.ServicioImplement;

import com.cristhian.SistemaInventario.Enums.NombreTipoDocumento;
import com.cristhian.SistemaInventario.Modelo.TipoDocumento;
import com.cristhian.SistemaInventario.Repositorio.TipoDocumentoRepository;
import com.cristhian.SistemaInventario.Service.ITipoDocumentoService;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



import java.util.List;
import java.util.Optional;
/**
 * Servicio encargado de la gestión de los tipos de documento.
 * Se encarga de listar, buscar y crear automáticamente los
 * tipos de documento permitidos en el sistema.
 */
@Service
@Transactional
public class TipoDocumentoImpl implements ITipoDocumentoService {

    /*private final TipoDocumentoRepository tipoDocumentoRepository;


    public TipoDocumentoImpl(TipoDocumentoRepository tipoDocumentoRepository) {
        this.tipoDocumentoRepository = tipoDocumentoRepository;
    }

    @PostConstruct
    public void inicializarTipoDocumento() {
        guardarTipoDocumentoAutomatico();
    }

    // -----------------------------------------------------------
    // 1. lISTAMOS LOS TIPOS DE DOCUMENTOS ACTIVOS
    // -----------------------------------------------------------
    @Override
    public List<TipoDocumento> listarTipoDocumentoActivo() {
        return tipoDocumentoRepository.findByActivoTrue();
    }

    // -----------------------------------------------------------
    // 2. BUSCAMOS EL TIPO DE DOCUEMNTO POR ID
    // -----------------------------------------------------------
    @Override
    public Optional<TipoDocumento> buscarTipoDucmentoId(int id) {
        return tipoDocumentoRepository.findById(id);
    }

    // -----------------------------------------------------------
    // 3. CREAMOS EL TIPO DE DOCUMENTO DE MANERA AUTOMATICA CUANDO SE EJECUTE EL PROYECTO LA RAZON
    // DE HACERLO ASI, ES EVITAR QUE EL USUARIO CREE TIPOS DE DOCUMENTOS QUEN NO EXISTEN EN REALIDAD.
    // -----------------------------------------------------------
    @Override
    @Transactional
    public void guardarTipoDocumentoAutomatico() {

        List<NombreTipoDocumento> tipoDocumetosPermitidos = List.of(
                NombreTipoDocumento.CEDULA_CIUDADANIA,
                NombreTipoDocumento.PASAPORTE,
                NombreTipoDocumento.PERMISO_PROTECCION_TEMPORAL,
                NombreTipoDocumento.CEDULA_CIUDADANIA_EXTRANJERA,
                NombreTipoDocumento.NIT
        );

        for(NombreTipoDocumento tipoDocumento : tipoDocumetosPermitidos){
            if(!tipoDocumentoRepository.existsByNombreTipoDocumento(tipoDocumento)) {
                TipoDocumento tipo = new TipoDocumento();
                tipo.setNombreTipoDocumento(tipoDocumento);
                // lógica automática para crear el impuesto.
                switch (tipoDocumento) {
                    case CEDULA_CIUDADANIA -> {
                        tipo.setSigla("CC");
                    }

                    case PASAPORTE -> {
                        tipo.setSigla("PP");
                    }

                    case PERMISO_PROTECCION_TEMPORAL -> {
                        tipo.setSigla("PPT");
                    }

                    case CEDULA_CIUDADANIA_EXTRANJERA -> {
                        tipo.setSigla("CCE");
                    }

                    case NIT -> {
                        tipo.setSigla("NIT");
                    }
                }

                tipo.setActivo(true);
                tipoDocumentoRepository.save(tipo);
            }
        }
    }

    // -----------------------------------------------------------
    // 5. DESACTIVAMOS EL TIPO DOCUMENTO
    // -----------------------------------------------------------
    @Override
    public void desactivarTipoDcomuento(int id) {
        TipoDocumento tipoDocumento = tipoDocumentoRepository.findById(id).orElse(null);
        if (tipoDocumento != null){
            tipoDocumento.setActivo(false);
            tipoDocumentoRepository.save(tipoDocumento);
        }
    }*/

    private final TipoDocumentoRepository tipoDocumentoRepository;

    public TipoDocumentoImpl(TipoDocumentoRepository tipoDocumentoRepository) {
        this.tipoDocumentoRepository = tipoDocumentoRepository;
    }

    /**
     * Inicializa los tipos de documento al arrancar la aplicación.
     * Evita la creación manual de tipos no válidos.
     */
    @PostConstruct
    public void inicializarTipoDocumento() {
        guardarTipoDocumentoAutomatico();
    }

    /**
     * Lista todos los tipos de documento activos.
     */
    @Override
    public List<TipoDocumento> listarTipoDocumentoActivo() {
        return tipoDocumentoRepository.findByActivoTrue();
    }

    /**
     * Busca un tipo de documento por su ID.
     */
    @Override
    public Optional<TipoDocumento> buscarTipoDucmentoId(int id) {
        return tipoDocumentoRepository.findById(id);
    }

    /**
     * Crea automáticamente los tipos de documento permitidos
     * cuando se inicia la aplicación.
     *
     * La razón de esta lógica es evitar que el usuario
     * cree tipos de documento inexistentes en la vida real.
     */
    @Override
    @Transactional
    public void guardarTipoDocumentoAutomatico() {

        List<NombreTipoDocumento> tipoDocumentosPermitidos = List.of(
                NombreTipoDocumento.CEDULA_CIUDADANIA,
                NombreTipoDocumento.PASAPORTE,
                NombreTipoDocumento.PERMISO_PROTECCION_TEMPORAL,
                NombreTipoDocumento.CEDULA_CIUDADANIA_EXTRANJERA,
                NombreTipoDocumento.NIT
        );

        for (NombreTipoDocumento tipoDocumento : tipoDocumentosPermitidos) {

            if (!tipoDocumentoRepository.existsByNombreTipoDocumento(tipoDocumento)) {

                TipoDocumento tipo = new TipoDocumento();
                tipo.setNombreTipoDocumento(tipoDocumento);

                // Asignación automática de siglas según el tipo de documento
                switch (tipoDocumento) {
                    case CEDULA_CIUDADANIA -> tipo.setSigla("CC");
                    case PASAPORTE -> tipo.setSigla("PP");
                    case PERMISO_PROTECCION_TEMPORAL -> tipo.setSigla("PPT");
                    case CEDULA_CIUDADANIA_EXTRANJERA -> tipo.setSigla("CCE");
                    case NIT -> tipo.setSigla("NIT");
                }

                tipo.setActivo(true);
                tipoDocumentoRepository.save(tipo);
            }
        }
    }

    /**
     * Desactiva un tipo de documento de forma lógica.
     */
    @Override
    public void desactivarTipoDcomuento(int id) {
        TipoDocumento tipoDocumento = tipoDocumentoRepository.findById(id).orElse(null);
        if (tipoDocumento != null) {
            tipoDocumento.setActivo(false);
            tipoDocumentoRepository.save(tipoDocumento);
        }
    }
}






