import * as moment from 'moment';
import { Component, OnInit } from '@angular/core';
import { ParameterService } from '../services/parameter.service';
import { Parameter } from '../app.model';
import { RotationService } from '../services/rotation.service';

@Component({
  selector: 'app-designation-countdown',
  templateUrl: './designation-countdown.component.html',
  styleUrls: ['./designation-countdown.component.css']
})
export class DesignationCountdownComponent implements OnInit {

  parameter: Parameter;

  countdown: string;

  constructor(
    private rotationService: RotationService,
    private parameterService: ParameterService
  ) { }

  ngOnInit() {
    this.parameterService.get()
      .subscribe(parameter => {
        this.parameter = parameter;
        this.calculateTimeToDesignation();
        setInterval(() => { this.calculateTimeToDesignation() }, 1000);
      });
    //TODO Go over reactive programming
    this.rotationService.rotationTimeEventBus$.subscribe(rotationTime => this.parameter.rotationTime = rotationTime);
    this.parameterService.parametersUpdateEventBus$.subscribe(parameter => this.parameter = parameter);
  }

  private rotationTime() {
    return moment(this.parameter.rotationTime, 'HH:mm');
  }

  private calculateTimeToDesignation() {
    let now = moment(new Date(), 'HH:mm:ss');
    let secondsToDesignation = 0;
    let rotationTimeInSeconds = this.toSeconds(this.rotationTime());
    let currentTimeInSeconds = this.toSeconds(now);
    if (this.rotationTime().isAfter(now)) {
      secondsToDesignation = rotationTimeInSeconds - currentTimeInSeconds;
    } else {
      secondsToDesignation = (86400 - currentTimeInSeconds) + rotationTimeInSeconds;
    }
    let days = Math.floor(secondsToDesignation / 86400);
    let hours = Math.floor((secondsToDesignation % 86400) / 3600);
    let minutes = Math.floor((secondsToDesignation % 86400) % 3600 / 60);
    let seconds = ((secondsToDesignation % 86400) % 3600) % 60;
    this.countdown = `${days}d ${hours}h ${minutes}m ${seconds}s`;
  }

  toSeconds(time: moment.Moment) {
    return time.hours() * 3600 + time.minutes() * 60 + time.seconds();
  }

}
