import { TestBed } from '@angular/core/testing';

import { RolPersonaService } from './rol-persona.service';

describe('RolPersonaService', () => {
  let service: RolPersonaService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(RolPersonaService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
