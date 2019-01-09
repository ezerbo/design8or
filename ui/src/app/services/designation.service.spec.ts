/* tslint:disable:no-unused-variable */

import { TestBed, async, inject } from '@angular/core/testing';
import { DesignationService } from './designation.service';

describe('Service: Designation', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [DesignationService]
    });
  });

  it('should ...', inject([DesignationService], (service: DesignationService) => {
    expect(service).toBeTruthy();
  }));
});
