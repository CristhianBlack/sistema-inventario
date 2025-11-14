import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TipoPersonaListaComponent } from './tipo-persona-lista.component';

describe('TipoPersonaListaComponent', () => {
  let component: TipoPersonaListaComponent;
  let fixture: ComponentFixture<TipoPersonaListaComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [TipoPersonaListaComponent]
    });
    fixture = TestBed.createComponent(TipoPersonaListaComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
