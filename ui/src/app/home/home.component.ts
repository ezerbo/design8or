declare var $: any;
import * as moment from 'moment';
import * as Stomp from '@stomp/stompjs';
import * as SockJS from 'sockjs-client';
import { Component, OnInit } from '@angular/core';
import { PoolService } from '../services/pool.service';
import { Pool, Pools, User, Error, Parameter } from '../app.model';
import { UserService } from '../services/user.service';
import { Validators, FormGroup, FormControl } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import { AmazingTimePickerService } from 'amazing-time-picker';
import { ParameterService } from '../services/parameter.service';
import { DesignationService } from '../services/designation.service';
import { environment } from '../../environments/environment';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {

  pools: Pools;

  currentPool: Pool;

  pastPools: Pool[] = [];

  lead: User;

  currentCandidate: User;

  candidates: User[] = [];

  error: Error;

  successMessage: string;

  selectedTime: string;

  parameter: Parameter;

  showRotationSaveBtn = false;

  designatedUser: User;

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

  constructor(
    private poolService: PoolService,
    private userService: UserService,
    private atp: AmazingTimePickerService,
    private designationService: DesignationService,
    private parameterService: ParameterService) {
    $(() => { $('[data-toggle="tooltip"]').tooltip(); });
    let stompClient = Stomp.Stomp.over(new SockJS(environment.wsEndpoint));
    stompClient.connect({}, () => {
      stompClient.subscribe('/designations', (message) => {
        console.log(`Message: ${message}`);
        if (message) {
          this.lead = JSON.parse(message.body);
          this.candidates = this.candidates.filter((c) => { return c.id != this.lead.id; });
        }
      });
    });
  }

  ngOnInit() {
    this.poolService.getAll()
      .subscribe(pools => {
        this.currentPool = pools.current;
        this.pastPools = pools.past;
      });

    this.userService.getCandidates()
      .subscribe(candidates => { this.candidates = candidates });

    this.userService.getLead()
      .subscribe(lead => { this.lead = lead });

    this.parameterService.get()
      .subscribe(parameter => { this.parameter = parameter });

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
        this.showSuccessMessage('User successfully created.');
      }, (errorResponse: HttpErrorResponse) => {
        this.processErrorResponse(errorResponse);
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
        this.showSuccessMessage('User successfully updated.');
      }, (errorResponse: HttpErrorResponse) => {
        this.processErrorResponse(errorResponse);
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
        this.showSuccessMessage('User successfully deleted.');
      }, (errorResponse: HttpErrorResponse) => {
        this.processErrorResponse(errorResponse);
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

  private showSuccessMessage(message: string) {
    setTimeout(() => { this.successMessage = null }, 3000);
    this.successMessage = message;
  }

  private processErrorResponse(errorResponse: any) {
    setTimeout(() => { this.error = null }, 3000);
    this.error = { description: errorResponse.error.description, message: errorResponse.error.message };
  }

  selectDesignationTime() {
    const amazingTimePicker = this.atp.open({ time: this.parameter.rotationTime });
    amazingTimePicker.afterClose().subscribe(selectedTime => {
      if (this.parameter.rotationTime.substring(0, 5) != selectedTime) {
        this.showRotationSaveBtn = true;
      }
      this.selectedTime = selectedTime;
    });
  }

  saveRotationTime() {
    this.parameter.rotationTime = this.selectedTime;
    this.parameterService.update(this.parameter)
      .subscribe(() => {
        this.showRotationSaveBtn = false;
        this.showSuccessMessage('Rotation time successfully updated.');
      });
  }

  rotationTime() {
    return moment(this.parameter.rotationTime, 'HH:mm');
  }

  cancelRotationTimeUpdate() {
    this.showRotationSaveBtn = false;
  }

  onUserSelectionChange(userId: number) {
    this.designatedUser = this.candidates.find((user) => { return user.id == userId });
  }

  onUserSelectionCanceled() {
    this.userSelectionForm.reset();
  }

  designate() {
    this.designationService.designate(this.designatedUser)
      .subscribe(user => {
        this.lead = user;
        this.candidates = this.candidates.filter((c) => { return c.id != user.id; });
        $('#manualDesignationModal').modal('hide');
        this.showSuccessMessage('Lead successfully designated.');
      }, (errorResponse: HttpErrorResponse) => {
        console.error(`${JSON.stringify(errorResponse)}`);
      });
  }

  get firstName() { return this.userForm.get('firstName'); }
  get lastName() { return this.userForm.get('lastName'); }
  get emailAddress() { return this.userForm.get('emailAddress'); }
}