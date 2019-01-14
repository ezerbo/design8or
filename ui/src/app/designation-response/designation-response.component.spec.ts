import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DesignationResponseComponent } from './designation-response.component';

describe('DesignationResponseComponent', () => {
  let component: DesignationResponseComponent;
  let fixture: ComponentFixture<DesignationResponseComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DesignationResponseComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DesignationResponseComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
