import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RolPersonaFormComponent } from './rol-persona-form.component';

describe('RolPersonaFormComponent', () => {
  let component: RolPersonaFormComponent;
  let fixture: ComponentFixture<RolPersonaFormComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [RolPersonaFormComponent]
    });
    fixture = TestBed.createComponent(RolPersonaFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
