import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, ParamMap } from '@angular/router';
import { DesignationService } from '../services/designation.service';
import { HttpErrorResponse } from '@angular/common/http';
import { DesignationResponse, Designation, Error } from '../app.model';
import { NgxSpinnerService } from 'ngx-spinner';

@Component({
  selector: 'app-designation-response',
  templateUrl: './designation-response.component.html',
  styleUrls: ['./designation-response.component.css']
})
export class DesignationResponseComponent implements OnInit {

  error: Error;
  designation: Designation;

  constructor(
    private route: ActivatedRoute,
    private spinner: NgxSpinnerService,
    private designationService: DesignationService) {}

  ngOnInit() {
    this.spinner.show();
    this.route.queryParamMap.subscribe(params => {
      let response = this.getDesignationResponseFromParams(params);
      this.designationService.processDesignationResponse(response).subscribe(designation => {
          this.designation = designation;
          this.spinner.hide();
        }, (err: HttpErrorResponse) => {
          this.error = err.error;
          this.spinner.hide();
        });
    });
  }

  private getDesignationResponseFromParams(params: ParamMap): DesignationResponse {
    return {
      token: params.get('token'),
      response: params.get('response'),
      emailAddress: params.get('email')
    }
  }

}