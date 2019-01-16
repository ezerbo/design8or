import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class SubscriptionService {

  constructor(private http: HttpClient) { }

  subscribe(subscription: any): Observable<any> {
    return this.http.post<any>(environment.subscriptionResource, subscription, { withCredentials: true });
  }
}