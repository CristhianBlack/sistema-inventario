import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UnidadMedidaListaComponent } from './unidad-medida-lista.component';

describe('UnidadMedidaListaComponent', () => {
  let component: UnidadMedidaListaComponent;
  let fixture: ComponentFixture<UnidadMedidaListaComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [UnidadMedidaListaComponent]
    });
    fixture = TestBed.createComponent(UnidadMedidaListaComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
