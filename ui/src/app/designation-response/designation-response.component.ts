import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { switchMap, map } from 'rxjs/operators';
import { DesignationService } from '../services/designation.service';
import { HttpErrorResponse } from '@angular/common/http';
import { AssignmentService } from '../services/assignment.service';
import { DesignationResponse } from '../app.model';

@Component({
  selector: 'app-designation-response',
  templateUrl: './designation-response.component.html',
  styleUrls: ['./designation-response.component.css']
})
export class DesignationResponseComponent implements OnInit {


  constructor(
    private route: ActivatedRoute,
    private assignmentService: AssignmentService,
    private designationService: DesignationService
  ) { }

  ngOnInit() {
    this.route.queryParamMap.subscribe(params => {
      let response: DesignationResponse = { token: params.get('token'), response: params.get('response') }
      this.designationService.processDesignationResponse(response)
        .subscribe(() => { console.log('designation successfully processed') });
    });
    this.assignmentService.assignmentEventBus$.subscribe(() => {
      console.log('User accepted designation');
    });
  }

}
