import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RolPersonaListaComponent } from './rol-persona-lista.component';

describe('RolPersonaListaComponent', () => {
  let component: RolPersonaListaComponent;
  let fixture: ComponentFixture<RolPersonaListaComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [RolPersonaListaComponent]
    });
    fixture = TestBed.createComponent(RolPersonaListaComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
