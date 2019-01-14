import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DesignatedComponent } from './designated.component';

describe('DesignatedComponent', () => {
  let component: DesignatedComponent;
  let fixture: ComponentFixture<DesignatedComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DesignatedComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DesignatedComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
