package com.cristhian.SistemaInventario.ServicioImplement;

import com.cristhian.SistemaInventario.DTO.ImpuestoDTO;
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

@Service
@Transactional
public class ImpuestoServiceImpl implements IImpuestoService {

    private final ImpuestoRepository impuestoRepository;

    public ImpuestoServiceImpl(ImpuestoRepository impuestoRepository) {
        this.impuestoRepository = impuestoRepository;
    }

    @PostConstruct
    public void inicializarImpuestos() {
        guardarImpuestoAutomatico();
    }

    @Override
    public List<Impuesto> listarImpuestosActivos() {
        return impuestoRepository.findByActivoTrue();
    }

    @Override
    public Optional<Impuesto> buscarImpuestoPorId(int id) {
        return impuestoRepository.findById(id);
    }

    @Override
    public void guardarImpuestoAutomatico() {

        List<TipoImpuesto> impuestosPermitidos = List.of(
                TipoImpuesto.IVA,
                TipoImpuesto.EXENTO

                );

        for (TipoImpuesto tipo : impuestosPermitidos) {

            if (!impuestoRepository.existsByTipoImpuesto(tipo)) {

                Impuesto impuesto = new Impuesto();
                impuesto.setTipoImpuesto(tipo);

                // lógica automática para crear el impuesto.
                switch (tipo) {
                    case IVA -> {
                        impuesto.setPorcentaje(BigDecimal.valueOf(0.19));
                    }

                    case EXENTO -> {
                        impuesto.setPorcentaje(BigDecimal.valueOf(0.00));
                    }
                }

                impuesto.setActivo(true);
                impuestoRepository.save(impuesto);
            }
        }
    }


    @Override
    public void borrarImpuesto(int id) {
        Impuesto impuesto = impuestoRepository.findById(id).orElse(null);
        if (impuesto != null) {
            impuesto.setActivo(false); // solo la marcamos como inactiva
            impuestoRepository.save(impuesto);
        }
    }
}
