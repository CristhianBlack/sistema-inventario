import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CiudadFormComponent } from './ciudad-form.component';

describe('CiudadFormComponent', () => {
  let component: CiudadFormComponent;
  let fixture: ComponentFixture<CiudadFormComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [CiudadFormComponent]
    });
    fixture = TestBed.createComponent(CiudadFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
