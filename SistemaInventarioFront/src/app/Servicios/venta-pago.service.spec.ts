import { TestBed } from '@angular/core/testing';

import { VentaPagoService } from './venta-pago.service';

describe('VentaPagoService', () => {
  let service: VentaPagoService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(VentaPagoService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
