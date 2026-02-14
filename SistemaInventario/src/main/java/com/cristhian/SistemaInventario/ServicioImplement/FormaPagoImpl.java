package com.cristhian.SistemaInventario.ServicioImplement;

import com.cristhian.SistemaInventario.DTO.FormaPagoDTO;
import com.cristhian.SistemaInventario.Excepciones.RecursoNoEncontradoException;
import com.cristhian.SistemaInventario.Modelo.FormaPago;
import com.cristhian.SistemaInventario.Enums.NombreFormaPago;
import com.cristhian.SistemaInventario.Enums.TipoPago;
import com.cristhian.SistemaInventario.Repositorio.FormaPagoRepository;
import com.cristhian.SistemaInventario.Service.IFormaPagoService;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class FormaPagoImpl implements IFormaPagoService {

    private final FormaPagoRepository formaPagoRepository;

    public FormaPagoImpl(FormaPagoRepository formaPagoRepository) {
        this.formaPagoRepository = formaPagoRepository;
    }

    @PostConstruct
    public void inicializarFormasPago() {
        guardarFormaPagoAutomatico();
    }

    @Override
    public List<FormaPago> listarFormaPagoActivo() {
        return formaPagoRepository.findByActivoTrue();
    }

    @Override
    public Optional<FormaPago> buscarFormaPagoByID(int id) {
        return formaPagoRepository.findById(id);
    }

    @Override
    public void guardarFormaPagoAutomatico() {

        List<NombreFormaPago> formasPermitidas = List.of(
                NombreFormaPago.EFECTIVO,
                NombreFormaPago.NEQUI,
                NombreFormaPago.DAVIPLATA,
                NombreFormaPago.TARJETA_CREDITO,
                NombreFormaPago.TARJETA_DEBITO,
                NombreFormaPago.TRANSFERENCIA
        );

        for (NombreFormaPago nombre : formasPermitidas) {

            if (!formaPagoRepository.existsByNombreFormaPago(nombre)) {

                FormaPago fp = new FormaPago();
                fp.setNombreFormaPago(nombre);

                // lógica automática para crear la forma de pago
                switch (nombre) {
                    case EFECTIVO -> {
                        fp.setTipoPago(TipoPago.INMEDIATO);
                        fp.setPermiteCuotas(false);
                        fp.setRequiereConfirmacion(false);
                    }

                    case NEQUI, DAVIPLATA, TRANSFERENCIA -> {
                        fp.setTipoPago(TipoPago.DIGITAL);
                        fp.setPermiteCuotas(false);
                        fp.setRequiereConfirmacion(true);
                    }

                    case TARJETA_DEBITO -> {
                        fp.setTipoPago(TipoPago.INMEDIATO);
                        fp.setPermiteCuotas(false);
                        fp.setRequiereConfirmacion(true);
                    }

                    case TARJETA_CREDITO -> {
                        fp.setTipoPago(TipoPago.CREDITO);
                        fp.setPermiteCuotas(true);
                        fp.setRequiereConfirmacion(true);
                    }
                }

                fp.setActivo(true);
                formaPagoRepository.save(fp);
            }
        }
    }

    @Override
    public FormaPago actualizarFormaPago(int id, FormaPagoDTO formaPagoDTO) {
        //Buscamos la forma de pago, si no existe mandamos un mensaje.
        FormaPago formaPago = formaPagoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe la forma de pago"
                ));

        // REGLAS DE NEGOCIO
        switch (formaPago.getNombreFormaPago()) {

            case EFECTIVO -> {
                throw new IllegalStateException(
                        "La forma de pago EFECTIVO no puede modificarse"
                );
            }

            case NEQUI, DAVIPLATA -> {
                // Solo se permite cambiar confirmación
                formaPago.setRequiereConfirmacion(formaPagoDTO.isRequiereConfirmacion());
            }

            case TARJETA_CREDITO -> {
                // Se permiten cuotas y confirmación
                formaPago.setPermiteCuotas(formaPagoDTO.isPermiteCuotas());
                formaPago.setRequiereConfirmacion(formaPagoDTO.isRequiereConfirmacion());
            }
        }


        return formaPagoRepository.save(formaPago);
    }

    @Override
    public void eliminarFormaPago(int id) {
        FormaPago formaPago = formaPagoRepository.findById(id).orElse(null);
        if (formaPago != null) {
            formaPago.setActivo(false);
            formaPagoRepository.save(formaPago);
        }
    }
}
