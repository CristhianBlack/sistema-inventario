import { ComponentFixture, TestBed } from '@angular/core/testing';

import { VentaPagoFormComponent } from './venta-pago-form.component';

describe('VentaPagoFormComponent', () => {
  let component: VentaPagoFormComponent;
  let fixture: ComponentFixture<VentaPagoFormComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [VentaPagoFormComponent]
    });
    fixture = TestBed.createComponent(VentaPagoFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
