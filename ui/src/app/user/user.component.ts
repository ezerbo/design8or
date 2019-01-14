declare var $: any;
import { Component, OnInit } from '@angular/core';
import { UserService } from '../services/user.service';
import { User } from '../app.model';
import { FormGroup, FormControl, Validators } from '@angular/forms';
import { DesignationService } from '../services/designation.service';
import { HttpErrorResponse } from '@angular/common/http';
import { MessageService, processErrorResponse } from '../services/message.service';

@Component({
  selector: 'app-user',
  templateUrl: './user.component.html',
  styleUrls: ['./user.component.css']
})
export class UserComponent implements OnInit {

  designatedUser: User;

  currentCandidate: User;

  candidates: User[] = [];

  userSelectionForm = new FormGroup({
    userId: new FormControl('', [Validators.required])
  });

  userForm = new FormGroup({
    id: new FormControl(''),
    firstName: new FormControl('', [Validators.required]),
    lastName: new FormControl('', [Validators.required]),
    emailAddress: new FormControl('', [Validators.required, Validators.email]),
    lead: new FormControl('')
  });

  constructor(private userService: UserService,
    private designationService: DesignationService,
    private msg: MessageService) {
    $(() => { $('[data-toggle="tooltip"]').tooltip(); });
    this.designationService.designationEventBus$.subscribe(() => { this.getCandidates() });
  }

  ngOnInit() {
    this.getCandidates();
    // this.designationService.designationEventBus$.subscribe(() => {
    //   this.getCandidates();
    // });
  }

  designate() {
    this.designationService.designate(this.designatedUser)
      .subscribe(user => {
        $('#manualDesignationModal').modal('hide');
        //this.candidates = this.candidates.filter((c) => { return c.id != user.id; });
        this.msg.emitSuccessEvent('Lead successfully designated.');
      }, (errorResponse: HttpErrorResponse) => {
        this.msg.emitErrorEvent(processErrorResponse(errorResponse));
      });
  }

  private getCandidates() {
    console.log('getting all candidates')
    this.userService.getCandidates()
      .subscribe(candidates => {
        this.candidates = [];
        this.candidates = candidates;
      });
  }

  saveUser() {
    const user: User = Object.assign({}, this.userForm.value);
    if (user.id) {
      this.updateUser(user);
    } else {
      this.createUser(user);
    }
  }

  private createUser(user: User) {
    this.userService.create(user)
      .subscribe(u => {
        this.candidates.push(u);
        this.candidates = this.candidates.slice();
        $('#newUserModal').modal('hide');
        this.userService.emitUserAddtionEvent(user);
        this.msg.emitSuccessEvent('User successfully created.');
      }, (errorResponse: HttpErrorResponse) => {
        this.msg.emitErrorEvent(processErrorResponse(errorResponse));
        $('#newUserModal').modal('hide');
      });
  }

  private updateUser(user: User) {
    this.userService.update(user)
      .subscribe(u => {
        this.candidates = this.candidates.filter((c) => { return c.id != user.id; });
        this.candidates.push(u);
        this.candidates = this.candidates.slice();
        $('#newUserModal').modal('hide');
        this.msg.emitSuccessEvent('User successfully updated.');
      }, (errorResponse: HttpErrorResponse) => {
        this.msg.emitErrorEvent(processErrorResponse(errorResponse));
        $('#newUserModal').modal('hide');
      });
  }

  onDeleteUser(candidate: User) {
    $('#confirmationModal').modal('show');
    this.currentCandidate = candidate;
  }

  deleteUser() {
    this.userService.delete(this.currentCandidate.id)
      .subscribe(() => {
        this.candidates = this.candidates.filter((c) => { return c.id != this.currentCandidate.id; });
        $('#confirmationModal').modal('hide');
        this.userService.emitUserDeletiontionEvent(this.currentCandidate);
        this.msg.emitSuccessEvent('User successfully deleted.');
      }, (errorResponse: HttpErrorResponse) => {
        this.msg.emitErrorEvent(processErrorResponse(errorResponse));
        $('#confirmationModal').modal('hide');
      });
  }

  onEditUser(candidate: User) {
    this.currentCandidate = candidate;
    this.userForm.setValue(Object.assign({}, candidate));
    $('#newUserModal').modal('show');
  }

  onAddUser() {
    this.userForm.reset();
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

  get firstName() { return this.userForm.get('firstName'); }
  get lastName() { return this.userForm.get('lastName'); }
  get emailAddress() { return this.userForm.get('emailAddress'); }

}