import { TestBed } from '@angular/core/testing';

import { KardexserviceService } from './kardexservice.service';

describe('KardexserviceService', () => {
  let service: KardexserviceService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(KardexserviceService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
