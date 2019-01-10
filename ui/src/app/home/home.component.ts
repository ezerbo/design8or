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

  constructor(private msg: MessageService) {
    let stompClient = Stomp.Stomp.over(new SockJS(environment.wsEndpoint));
    stompClient.connect({}, () => {
      stompClient.subscribe('/designations', (message) => {
        if (message) {
          this.msg.emitDesignationEvent(JSON.parse(message.body));
        }
      });
    });
  }

  ngOnInit() { }

}