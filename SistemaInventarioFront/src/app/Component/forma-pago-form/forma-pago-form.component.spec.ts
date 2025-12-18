import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FormaPagoFormComponent } from './forma-pago-form.component';

describe('FormaPagoFormComponent', () => {
  let component: FormaPagoFormComponent;
  let fixture: ComponentFixture<FormaPagoFormComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [FormaPagoFormComponent]
    });
    fixture = TestBed.createComponent(FormaPagoFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
