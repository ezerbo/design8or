declare var $: any;
import { Component, OnInit } from '@angular/core';
import { UserService } from '../services/user.service';
import { User } from '../app.model';
import { FormGroup, FormControl, Validators } from '@angular/forms';
import { DesignationService } from '../services/designation.service';
import { HttpErrorResponse } from '@angular/common/http';
import { MessageService, processErrorResponse } from '../services/message.service';
import { NgxSpinnerService } from 'ngx-spinner';

@Component({
  selector: 'app-candidate',
  templateUrl: './candidate.component.html',
  styleUrls: ['./candidate.component.css']
})
export class CandidateComponent implements OnInit {

  designatedUser: User;
  currentCandidate: User;
  candidates: User[] = [];

  userSelectionForm = new FormGroup({
    userId: new FormControl('', [Validators.required])
  });

  constructor(
    private msg: MessageService,
    private userService: UserService,
    private spinner: NgxSpinnerService,
    private designationService: DesignationService) {}

  ngOnInit() {
    this.getCandidates();
    this.designationService.designationEventBus$.subscribe(() => { this.getCandidates() });
  }

  designate() {
    this.spinner.show();
    this.designationService.designate(this.designatedUser)
      .subscribe(() => {
        $('#manualDesignationModal').modal('hide');
        this.msg.emitSuccessEvent('Lead successfully designated.');
        this.spinner.hide();
      }, (errorResponse: HttpErrorResponse) => {
        this.msg.emitErrorEvent(processErrorResponse(errorResponse));
        this.spinner.hide();
      });
  }

  private getCandidates() {
    this.userService.getCandidates()
      .subscribe(candidates => {
        this.candidates = [];
        this.candidates = candidates;
      });
  }

  onDesignate() {
    this.userSelectionForm.reset();
  }

  onUserSelectionChange(userId: number) {
    this.designatedUser = this.candidates.find((user) => { return user.id == userId });
  }

  onUserSelectionCanceled() {
    this.userSelectionForm.reset();
  }


}