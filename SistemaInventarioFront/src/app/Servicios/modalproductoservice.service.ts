import { Injectable } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { Observable } from 'rxjs';
import { ProductoFormComponent } from '../Component/producto-form/producto-form.component';

@Injectable({
  providedIn: 'root'
})
export class ModalProductoService {

 constructor(private dialog: MatDialog) {}

  abrirModal(): Observable<any> {
    const dialogRef = this.dialog.open(ProductoFormComponent, {
      width: '600px',
      height : '550px',
      panelClass: 'modal-frontal',
      restoreFocus: false,
      autoFocus: true,
      hasBackdrop: true
    });

    return dialogRef.afterClosed();
  }
}
