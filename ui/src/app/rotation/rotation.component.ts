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

  constructor(
    private messageService: MessageService,
    private atp: AmazingTimePickerService,
    private parameterService: ParameterService
  ){ }

  ngOnInit() {
    this.parameterService.get()
    .subscribe(parameter => { this.parameter = parameter });
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

}