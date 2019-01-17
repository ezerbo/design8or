import * as moment from 'moment';
import { Component, OnInit } from '@angular/core';
import { AmazingTimePickerService } from 'amazing-time-picker';
import { ParameterService } from '../services/parameter.service';
import { Parameter } from '../app.model';
import { MessageService } from '../services/message.service';
import { RotationService } from '../services/rotation.service';
import { NgxSpinnerService } from 'ngx-spinner';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-rotation',
  templateUrl: './rotation.component.html',
  styleUrls: ['./rotation.component.css']
})
export class RotationComponent implements OnInit {

  selectedTime: string;

  parameter: Parameter;

  showRotationSaveBtn = false;

  constructor(
    private msg: MessageService,
    private spinner: NgxSpinnerService,
    private atp: AmazingTimePickerService,
    private rotationService: RotationService,
    private parameterService: ParameterService
  ) { }

  ngOnInit() {
    this.parameterService.get()
      .subscribe(parameter => {
        this.parameter = parameter;
      });
  }

  saveRotationTime() {
    this.spinner.show();
    this.parameter.rotationTime = this.selectedTime;
    this.parameterService.update(this.parameter)
      .subscribe(() => {
        this.showRotationSaveBtn = false;
        this.msg.emitSuccessEvent('Rotation time successfully updated.');
        this.rotationService.emitRotationTimeUpdateEvent(this.selectedTime);
        this.spinner.hide();
      }, (err: HttpErrorResponse) => {
        this.spinner.hide();
        console.error(`${JSON.stringify(err)}`);
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
      if (this.parameter.rotationTime.substring(0, 5) != selectedTime) { //When trigger time is updated
        this.showRotationSaveBtn = true;
      }
      this.selectedTime = selectedTime;
    });
  }

}