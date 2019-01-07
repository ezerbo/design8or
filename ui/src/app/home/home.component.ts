declare var $ :any; 
import { Component, OnInit } from '@angular/core';
import { PoolService } from '../services/pool.service';
import { Pool, Pools, User, Error } from '../app.model';
import { UserService } from '../services/user.service';
import { Validators, FormGroup, FormControl } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';

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

  candidates: User[] = [];

  error: Error;

  successMessage: string;

  userForm = new FormGroup({
    firstName: new FormControl('', [Validators.required]),
    lastName: new FormControl('', [Validators.required]),
    emailAddress: new FormControl('', [Validators.required, Validators.email])
  });

  constructor(
    private poolService: PoolService,
    private userService: UserService
  ) { }

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
  }

  saveUser() {
    const user: User = Object.assign({}, this.userForm.value);
    this.userService.create(user)
      .subscribe(u => { 
        this.candidates.push(u);
        this.candidates = this.candidates.slice();
        $('.modal').modal('hide');
        setTimeout(() => { this.successMessage = null }, 3000);
        this.successMessage = 'User successfully created.';
      }, (errorResponse: HttpErrorResponse) => {
        this.error = {description: errorResponse.error.description, message: errorResponse.error.message};
      });
  }

  onAddUser() {
    this.userForm.reset();
  }

  get firstName() { return this.userForm.get('firstName'); }
  get lastName() { return this.userForm.get('lastName'); }
  get emailAddress() { return this.userForm.get('emailAddress'); }
}