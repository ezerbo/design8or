import { Component, OnInit } from '@angular/core';
import { Error } from '../app.model';
import { MessageService } from '../services/message.service';

@Component({
  selector: 'app-message',
  templateUrl: './message.component.html',
  styleUrls: ['./message.component.css']
})
export class MessageComponent implements OnInit {

  error: Error;
  success: string;

  constructor(private msg: MessageService) { }

  ngOnInit() {
    this.msg.successMessageEventBus$.subscribe(success => this.onSuccess(success));
    this.msg.errorMessageEventBus$.subscribe(error => this.onError(error));
  }

  private onSuccess(success: string) {
    setTimeout(() => this.success = null, 3000);
    this.success = success;
  }

  private onError(error: Error) {
    setTimeout(() => this.error = null, 3000);
    this.error = error;
  }

}