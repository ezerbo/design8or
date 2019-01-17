import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {

  today = new Date();
  showParametersPanel = false;
  showCandidatesPanel = true;
  showUsersPanel = false;

  constructor() { }

  ngOnInit() { }

  showCandidates() {
    this.showCandidatesPanel = true;
    this.showParametersPanel = false;
    this.showUsersPanel = false;
  }

  showParameters() {
    this.showCandidatesPanel = false;
    this.showParametersPanel = true;
    this.showUsersPanel = false;
  }

  showUsers() {
    this.showCandidatesPanel = false;
    this.showParametersPanel = false;
    this.showUsersPanel = true;
  }

}