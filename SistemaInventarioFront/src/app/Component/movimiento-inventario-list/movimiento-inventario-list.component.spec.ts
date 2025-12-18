import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MovimientoInventarioListComponent } from './movimiento-inventario-list.component';

describe('MovimientoInventarioListComponent', () => {
  let component: MovimientoInventarioListComponent;
  let fixture: ComponentFixture<MovimientoInventarioListComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [MovimientoInventarioListComponent]
    });
    fixture = TestBed.createComponent(MovimientoInventarioListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
