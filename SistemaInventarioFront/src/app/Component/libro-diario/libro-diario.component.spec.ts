import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LibroDiarioComponent } from './libro-diario.component';

describe('LibroDiarioComponent', () => {
  let component: LibroDiarioComponent;
  let fixture: ComponentFixture<LibroDiarioComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [LibroDiarioComponent]
    });
    fixture = TestBed.createComponent(LibroDiarioComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
