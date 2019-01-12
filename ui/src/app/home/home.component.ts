import * as Stomp from '@stomp/stompjs';
import * as SockJS from 'sockjs-client';
import { Component, OnInit } from '@angular/core';
import { environment } from '../../environments/environment';
import { MessageService } from '../services/message.service';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {

  today = new Date();

  stompClient = Stomp.Stomp.over(new SockJS(environment.wsEndpoint));

  constructor(private msg: MessageService) {
    this.stompClient.connect({}, () => {
      this.stompClient.subscribe('/designations', (message) => {
        if (message) {
          this.msg.emitDesignationEvent(this.parseMessage(message));
        }
      });
      this.stompClient.subscribe('/pools', (message) => {
        if (message) {
          this.msg.emitPoolStartEvent(this.parseMessage(message));
        }
      })
    });
  }

  ngOnInit() {
  }

  private parseMessage(message: any) {
    return JSON.parse(message.body);
  }

}