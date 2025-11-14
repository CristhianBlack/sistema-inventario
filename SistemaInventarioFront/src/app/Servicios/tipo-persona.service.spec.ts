import { TestBed } from '@angular/core/testing';

import { TipoPersonaService } from './tipo-persona.service';

describe('TipoPersonaService', () => {
  let service: TipoPersonaService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(TipoPersonaService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
