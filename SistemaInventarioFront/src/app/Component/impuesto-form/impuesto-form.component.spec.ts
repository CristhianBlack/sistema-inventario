import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ImpuestoFormComponent } from './impuesto-form.component';

describe('ImpuestoFormComponent', () => {
  let component: ImpuestoFormComponent;
  let fixture: ComponentFixture<ImpuestoFormComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ImpuestoFormComponent]
    });
    fixture = TestBed.createComponent(ImpuestoFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
