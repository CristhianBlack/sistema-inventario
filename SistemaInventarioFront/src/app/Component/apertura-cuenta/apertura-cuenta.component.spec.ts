import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AperturaCuentaComponent } from './apertura-cuenta.component';

describe('AperturaCuentaComponent', () => {
  let component: AperturaCuentaComponent;
  let fixture: ComponentFixture<AperturaCuentaComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [AperturaCuentaComponent]
    });
    fixture = TestBed.createComponent(AperturaCuentaComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
