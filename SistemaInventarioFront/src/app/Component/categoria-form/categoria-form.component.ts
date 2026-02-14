import {
  Component,
  EventEmitter,
  Input,
  Output,
  OnInit,
  SimpleChanges,
} from '@angular/core';
import { Categoria } from 'src/app/Modelos/categoria';
import { CategoriaService } from 'src/app/Servicios/categoria.service';
import { ToastrService } from 'ngx-toastr';
import { NgForm } from '@angular/forms';

@Component({
  selector: 'app-categoria-form',
  templateUrl: './categoria-form.component.html',
  styleUrls: ['./categoria-form.component.css'],
})
export class CategoriaFormComponent implements OnInit {
  /**
   * Categoría recibida desde el componente padre.
   * - Si viene con datos → modo edición
   * - Si es null o undefined → modo creación
   */
  @Input() categoria?: Categoria | null = null;

  /**
   * Evento que se emite cuando la categoría
   * se guarda correctamente (crear o editar)
   * Permite al componente padre actualizar la lista
   */
  @Output() formGuardado = new EventEmitter<void>();

  /**
   * Modelo interno del formulario.
   * Se utiliza una copia para no modificar directamente el @Input
   */
  formModel: Categoria = new Categoria();

  constructor(
    private categoriaService: CategoriaService,
    private toastr: ToastrService,
  ) {}

  /**
   * Ciclo de vida del componente
   * Se ejecuta al inicializar
   * Si existe una categoría, se clona en el modelo del formulario
   */
  ngOnInit(): void {
    if (this.categoria) {
      this.formModel = { ...this.categoria };
    }
  }

  /**
   * Detecta cambios en el @Input categoria
   * Permite reutilizar el mismo formulario
   * tanto para crear como para editar
   */
  ngOnChanges(changes: SimpleChanges): void {
    if (changes['categoria']) {
      if (this.categoria) {
        // Modo edición → clonar datos de la categoría seleccionada
        this.formModel = { ...this.categoria };
      } else {
        // Modo creación → limpiar formulario
        this.formModel = new Categoria();
      }
    }
  }

  /**
   * Maneja el envío del formulario
   * Decide si se crea o edita una categoría
   * según la existencia del idCategoria
   */
  onSubmit(): void {
    // EDICIÓN
    if (this.formModel.idCategoria) {
      this.categoriaService
        .editarCategoria(this.formModel.idCategoria, this.formModel)
        .subscribe({
          next: () => {
            this.toastr.success('Categoría actualizada correctamente', 'Éxito');
            this.formGuardado.emit();
          },
          error: (err) => {
            console.error(err);
            this.toastr.error('Error al editar categoría', 'Error');
          },
        });
    }
    // CREACIÓN
    else {
      this.categoriaService.agregarCategoria(this.formModel).subscribe({
        next: () => {
          this.toastr.success('Categoría agregada correctamente', 'Éxito');
          this.formGuardado.emit();
        },
        error: (err) => {
          console.error(err);
          // Muestra el mensaje enviado por el backend
          this.toastr.error(err.mensaje, 'Error');
        },
      });
    }
  }

  limpiarFormulario(formCategoria?: NgForm): void {
    this.formModel = new Categoria(); //  reinicia el modelo
    formCategoria?.resetForm();       //  limpia visualmente los inputs
  }
}
