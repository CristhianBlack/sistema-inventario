import {
  Component,
  EventEmitter,
  Input,
  OnInit,
  Output,
  SimpleChanges,
} from '@angular/core';
import { NgForm } from '@angular/forms';
import { ToastrService } from 'ngx-toastr';
import { UnidadMedida } from 'src/app/Modelos/unidad-medida';
import { UnidadMedidaService } from 'src/app/Servicios/unidad-medida.service';

@Component({
  selector: 'app-unidad-medida-form',
  templateUrl: './unidad-medida-form.component.html',
  styleUrls: ['./unidad-medida-form.component.css'],
})
export class UnidadMedidaFormComponent implements OnInit {
  /**
   * Unidad de medida recibida desde el componente padre.
   * Si viene con datos → modo edición.
   * Si viene null o undefined → modo creación.
   */
  @Input() unidadMedida?: UnidadMedida | null = null;

  /**
   * Evento emitido cuando se guarda correctamente
   * para que el padre refresque la tabla/listado.
   */
  @Output() formGuardado = new EventEmitter<void>();

  /**
   * Modelo interno del formulario.
   * Se usa una copia para no modificar directamente el @Input().
   */
  formModel: UnidadMedida = new UnidadMedida();

  constructor(
    private unidadMedidaService: UnidadMedidaService,
    private toastr: ToastrService,
  ) {}

  /**
   * Inicializa el componente.
   * Si llega una unidad por Input, se clona en el modelo local.
   */
  ngOnInit(): void {
    if (this.unidadMedida) {
      this.formModel = { ...this.unidadMedida };
    }
  }

  /**
   * Detecta cambios en el @Input() unidadMedida.
   * - Si viene una unidad → cargar datos para edición
   * - Si viene null → limpiar formulario (crear)
   */
  ngOnChanges(changes: SimpleChanges): void {
    if (changes['unidadMedida'] && this.unidadMedida) {
      this.formModel = { ...this.unidadMedida };
    } else {
      this.formModel = new UnidadMedida();
    }
  }

  /**
   * Envía el formulario para crear o editar una unidad de medida.
   */
  onSubmit(formUnidadMedida: NgForm): void {
    // ======================
    // EDITAR
    // ======================
    if (this.formModel.idUnidadMedida) {
      this.unidadMedidaService
        .editarUnidadMedida(this.formModel.idUnidadMedida, this.formModel)
        .subscribe({
          next: () => {
            this.toastr.success(
              'Unidad de medida actualizada correctamente',
              'Éxito',
            );
            this.formGuardado.emit(); // notifica al padre
            this.limpiarFormulario(formUnidadMedida);
          },
          error: () => {
            this.toastr.error('Error al editar la unidad de medida', 'Error');
          },
        });
    }

    // ======================
    // CREAR
    // ======================
    else {
      this.unidadMedidaService.guardarUnidadMedida(this.formModel).subscribe({
        next: () => {
          this.toastr.success(
            'Unidad de medida agregada correctamente',
            'Éxito',
          );
          this.formGuardado.emit(); // notifica al padre
          this.limpiarFormulario(formUnidadMedida);
        },
        error: (err) => {
          this.toastr.error(
            err.message || 'Error al guardar la unidad',
            'Error',
          );
        },
      });
    }
  }

  /**
   * Limpia el formulario y reinicia el modelo.
   */
  limpiarFormulario(formUnidadMedida?: NgForm): void {
    this.formModel = new UnidadMedida(); // reinicia datos
    formUnidadMedida?.resetForm(); // limpia inputs visualmente
  }
}
