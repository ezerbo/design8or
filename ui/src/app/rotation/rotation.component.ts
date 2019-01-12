import * as moment from 'moment';
import { Component, OnInit } from '@angular/core';
import { AmazingTimePickerService } from 'amazing-time-picker';
import { ParameterService } from '../services/parameter.service';
import { Parameter } from '../app.model';
import { MessageService } from '../services/message.service';

@Component({
  selector: 'app-rotation',
  templateUrl: './rotation.component.html',
  styleUrls: ['./rotation.component.css']
})
export class RotationComponent implements OnInit {

  parameter: Parameter;

  showRotationSaveBtn = false;

  selectedTime: string;

  countdown: string;

  constructor(
    private messageService: MessageService,
    private atp: AmazingTimePickerService,
    private parameterService: ParameterService
  ) { }

  ngOnInit() {
    this.parameterService.get()
      .subscribe(parameter => {
        this.parameter = parameter;
        this.calculateTimeToDesignation();
        setInterval(() => { this.calculateTimeToDesignation() }, 1000);
      });
    
  }

  saveRotationTime() {
    this.parameter.rotationTime = this.selectedTime;
    this.parameterService.update(this.parameter)
      .subscribe(() => {
        this.showRotationSaveBtn = false;
        this.messageService.emitSuccessEvent('Rotation time successfully updated.');
      });
  }

  rotationTime() {
    return moment(this.parameter.rotationTime, 'HH:mm');
  }

  cancelRotationTimeUpdate() {
    this.showRotationSaveBtn = false;
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