import { ComponentFixture, TestBed } from '@angular/core/testing';

import { VentaListaComponent } from './venta-lista.component';

describe('VentaListaComponent', () => {
  let component: VentaListaComponent;
  let fixture: ComponentFixture<VentaListaComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [VentaListaComponent]
    });
    fixture = TestBed.createComponent(VentaListaComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
