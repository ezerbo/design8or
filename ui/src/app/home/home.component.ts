import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {

  today = new Date();

  showParametersPanel = false;

  constructor() { }

  ngOnInit() { }

  onShowParameters() {
    this.showParametersPanel = true;
  }

  onHideParameters() {
    this.showParametersPanel = false;
  }


}