import { TestBed, inject } from '@angular/core/testing';

import { RotationService } from './rotation.service';

describe('RotationService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [RotationService]
    });
  });

  it('should be created', inject([RotationService], (service: RotationService) => {
    expect(service).toBeTruthy();
  }));
});
