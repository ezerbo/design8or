/* tslint:disable:no-unused-variable */
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { DebugElement } from '@angular/core';

import { LeadUserComponent } from './lead-user.component';

describe('LeadUserComponent', () => {
  let component: LeadUserComponent;
  let fixture: ComponentFixture<LeadUserComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ LeadUserComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(LeadUserComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
