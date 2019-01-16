import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DesignatedUserComponent } from './designated-user.component';

describe('DesignatedComponent', () => {
  let component: DesignatedUserComponent;
  let fixture: ComponentFixture<DesignatedUserComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DesignatedUserComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DesignatedUserComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
