import { Component, OnInit } from '@angular/core';
import { Designation } from '../app.model';
import { DesignationService } from '../services/designation.service';

@Component({
  selector: 'app-designated',
  templateUrl: './designated.component.html',
  styleUrls: ['./designated.component.css']
})
export class DesignatedComponent implements OnInit {

  designation: Designation;

  constructor(private designationService: DesignationService) {}

  ngOnInit() {
    this.designationService.getCurrent()
      .subscribe(designation => { this.designation = designation });

    this.designationService.designationEventBus$
      .subscribe(designation => { this.designation = designation; });
  }

}