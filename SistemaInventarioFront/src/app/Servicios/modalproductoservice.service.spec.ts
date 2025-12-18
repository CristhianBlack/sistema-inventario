import { TestBed } from '@angular/core/testing';

import { ModalproductoserviceService } from './modalproductoservice.service';

describe('ModalproductoserviceService', () => {
  let service: ModalproductoserviceService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ModalproductoserviceService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
