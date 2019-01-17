declare var $: any;
import { Component, OnInit } from '@angular/core';
import { UserService } from '../services/user.service';
import { User } from '../app.model';
import { FormGroup, FormControl, Validators } from '@angular/forms';
import { NgxSpinnerService } from 'ngx-spinner';
import { MessageService, processErrorResponse } from '../services/message.service';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-user',
  templateUrl: './user.component.html',
  styleUrls: ['./user.component.css']
})
export class UserComponent implements OnInit {

  user: User;
  users: User[] = [];

  userForm = new FormGroup({
    id: new FormControl(''),
    firstName: new FormControl('', [Validators.required]),
    lastName: new FormControl('', [Validators.required]),
    emailAddress: new FormControl('', [Validators.required, Validators.email]),
    lead: new FormControl('')
  });

  constructor(
    private msg: MessageService,
    private userService: UserService,
    private spinner: NgxSpinnerService
  ) { }

  ngOnInit() {
    $(() => {
      $('[data-toggle="tooltip"]').tooltip();
    });
    this.userService.getAll()
      .subscribe(users => this.users = users);
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
    this.spinner.show();
    this.userService.create(user)
      .subscribe(u => {
        this.users.push(u);
        this.users = this.users.slice();
        $('#newUserModal').modal('hide');
        this.userService.emitUserAddtionEvent(user);
        this.msg.emitSuccessEvent('User successfully created.');
        this.spinner.hide();
      }, (errorResponse: HttpErrorResponse) => {
        this.msg.emitErrorEvent(processErrorResponse(errorResponse));
        $('#newUserModal').modal('hide');
        this.spinner.hide();
      });
  }

  private updateUser(user: User) {
    this.spinner.show();
    this.userService.update(user).subscribe(u => {
        this.users = this.users.filter((c) => { return c.id != user.id; });
        this.users.push(u);
        this.users = this.users.slice();
        $('#newUserModal').modal('hide');
        this.msg.emitSuccessEvent('User successfully updated.');
        this.spinner.hide();
      }, (errorResponse: HttpErrorResponse) => {
        this.msg.emitErrorEvent(processErrorResponse(errorResponse));
        $('#newUserModal').modal('hide');
        this.spinner.hide();
      });
  }

  onDeleteUser(user: User) {
    $('#confirmationModal').modal('show');
    this.user = user;
  }

  deleteUser() {
    this.userService.delete(this.user.id)
      .subscribe(() => {
        this.users = this.users.filter((c) => { return c.id != this.user.id; });
        $('#confirmationModal').modal('hide');
        this.userService.emitUserDeletiontionEvent(this.user);
        this.msg.emitSuccessEvent('User successfully deleted.');
      }, (errorResponse: HttpErrorResponse) => {
        this.msg.emitErrorEvent(processErrorResponse(errorResponse));
        $('#confirmationModal').modal('hide');
      });
  }

  onEditUser(user: User) {
    this.user = user;
    this.userForm.setValue(Object.assign({}, user));
    $('#newUserModal').modal('show');
  }

  onAddUser() {
    this.userForm.reset();
  }

  get firstName() { return this.userForm.get('firstName'); }
  get lastName() { return this.userForm.get('lastName'); }
  get emailAddress() { return this.userForm.get('emailAddress'); }

}
