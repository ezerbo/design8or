import { Component, OnInit } from '@angular/core';
import { Designation } from '../app.model';
import { DesignationService } from '../services/designation.service';
import { AssignmentService } from '../services/assignment.service';

@Component({
  selector: 'app-designated-user',
  templateUrl: './designated-user.component.html',
  styleUrls: ['./designated-user.component.css']
})
export class DesignatedUserComponent implements OnInit {

  designation: Designation;

  constructor(
    private assignmentService: AssignmentService,
    private designationService: DesignationService
  ) {}

  ngOnInit() {
    this.designationService.getCurrent()
      .subscribe(designation => { this.designation = designation });

    this.designationService.designationEventBus$
      .subscribe(designation => { this.designation = designation; });

    this.assignmentService.assignmentEventBus$
      .subscribe(() => { this.designation = null }); //Will hide designated user panel when a new lead is assigned
  }

}