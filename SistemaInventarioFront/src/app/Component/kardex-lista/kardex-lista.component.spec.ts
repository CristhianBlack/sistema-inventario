import { ComponentFixture, TestBed } from '@angular/core/testing';

import { KardexListaComponent } from './kardex-lista.component';

describe('KardexListaComponent', () => {
  let component: KardexListaComponent;
  let fixture: ComponentFixture<KardexListaComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [KardexListaComponent]
    });
    fixture = TestBed.createComponent(KardexListaComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
