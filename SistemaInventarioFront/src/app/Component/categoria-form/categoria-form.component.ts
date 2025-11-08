import { Component, EventEmitter, Input, Output, OnInit, SimpleChanges } from '@angular/core';
import { Categoria } from 'src/app/Modelos/categoria';
import { CategoriaService } from 'src/app/Servicios/categoria.service';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-categoria-form',
  templateUrl: './categoria-form.component.html',
  styleUrls: ['./categoria-form.component.css']
})
export class CategoriaFormComponent implements OnInit {

  // ✅ Recibe una categoría cuando se va a editar (puede venir vacía si es "crear")
  @Input() categoria?: Categoria | null = null;

  // ✅ Emite un evento al guardar, para que el padre (lista) actualice la tabla
  @Output() formGuardado = new EventEmitter<void>();

  // ✅ Modelo interno usado en el formulario (para no alterar directamente el @Input)
  formModel: Categoria = new Categoria();

  constructor(
    private categoriaService: CategoriaService,
    private toastr: ToastrService
  ) {}

  ngOnInit(): void {
    // Si se recibe una categoría por Input, la clonamos en el modelo local
    if (this.categoria) {
      this.formModel = { ...this.categoria };
    }
  }

   // ✅ Detecta cuando cambia el Input() y actualiza el modelo
   // 🔄 Detecta cambios en el input y actualiza el formulario
  ngOnChanges(changes: SimpleChanges): void {
    if (changes['categoria'] && this.categoria) {
      this.formModel = { ...this.categoria };
    } else {
      this.formModel = new Categoria();
    }
  }

  onSubmit(): void {
    if (this.formModel.idCategoria) {
      // ✏️ Editar
      this.categoriaService.editarCategoria(this.formModel.idCategoria, this.formModel).subscribe({
        next: () => {
          this.toastr.success('Categoría actualizada correctamente', 'Éxito');
          this.formGuardado.emit();
        },
        error: (err) => {
          console.error(err);
          this.toastr.error('Error al editar categoría', 'Error');
        }
      });
    } else {
      // ➕ Agregar
      this.categoriaService.agregarCategoria(this.formModel).subscribe({
        next: () => {
          this.toastr.success('Categoría agregada correctamente', 'Éxito');
          this.formGuardado.emit();
        },
        error: (err) => {
          console.error(err);
          this.toastr.error('Error al agregar categoría', 'Error');
        }
      });
    }
  }
}

