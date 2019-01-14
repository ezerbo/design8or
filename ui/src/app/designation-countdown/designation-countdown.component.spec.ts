import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DesignationCountdownComponent } from './designation-countdown.component';

describe('DesignationCountdownComponent', () => {
  let component: DesignationCountdownComponent;
  let fixture: ComponentFixture<DesignationCountdownComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DesignationCountdownComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DesignationCountdownComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
