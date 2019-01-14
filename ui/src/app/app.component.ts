import * as Stomp from '@stomp/stompjs';
import * as SockJS from 'sockjs-client';
import { Component } from '@angular/core';
import { SwPush } from '@angular/service-worker';
import { environment } from 'src/environments/environment';
import { SubscriptionService } from './services/subscription.service';
import { PoolService } from './services/pool.service';
import { AssignmentService } from './services/assignment.service';
import { DesignationService } from './services/designation.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {

  stompClient = Stomp.Stomp.over(new SockJS(environment.wsEndpoint));

  title = 'desig8or';

  private serverPublicKey = 'BF_aZ-cmliVi5qbtxhGZrxxGZtEys0aLjZLhmtrboGtGiV__OxZa_emH2spKWNx8lZni_11a4oUJCfuEdT8x5rg';


  constructor(
    private swPush: SwPush,
    private poolService: PoolService,
    private assignmentService: AssignmentService,
    private designationService: DesignationService,
    private subscriptionService: SubscriptionService) {
    
    if ('serviceWorker' in navigator && environment.production) {
      navigator.serviceWorker
        .register('./ngsw-worker.js')
        .then(function () { console.log('Service Worker Registered'); })
        .catch((error) => { console.log('Unable to register Service Worker', error) });

      this.swPush.requestSubscription({ serverPublicKey: this.serverPublicKey })
      .then(subscription => {
        this.subscriptionService.subscribe(subscription).subscribe(() => {
          console.log('Successfully subscribed to notifications');
        });
      })
      .catch(error => { console.error('Subscription to notifications denied', error) });
    }

    this.stompClient.connect({}, () => {
      this.stompClient.subscribe('/designations', (message) => {
        if (message) {
          this.designationService.emitDesignationEvent(this.parseMessage(message));
        }
      });
      this.stompClient.subscribe('/assignments', (message) => {
        if (message) {
          this.assignmentService.emitAssignmentEvent(this.parseMessage(message));
        }
      });
      this.stompClient.subscribe('/pools', (message) => {
        if (message) {
          this.poolService.emitPoolStartEvent(this.parseMessage(message));
        }
      })
    });
  }
  private parseMessage(message: any) {
    return JSON.parse(message.body);
  }

}
