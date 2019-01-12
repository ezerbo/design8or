import { Component } from '@angular/core';
import { SwPush } from '@angular/service-worker';
import { environment } from 'src/environments/environment';
import { SubscriptionService } from './services/subscription.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {

  private serverPublicKey = 'BF_aZ-cmliVi5qbtxhGZrxxGZtEys0aLjZLhmtrboGtGiV__OxZa_emH2spKWNx8lZni_11a4oUJCfuEdT8x5rg';

  title = 'desig8or';

  constructor(private swPush: SwPush, private subscriptionService: SubscriptionService) {
    
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
  }


}
