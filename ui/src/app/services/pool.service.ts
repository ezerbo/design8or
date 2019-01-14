import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, Subject } from 'rxjs';
import { Pools, Pool } from '../app.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class PoolService {

  private poolStartEvent = new Subject<Pool>();

  poolStartEventBus$ = this.poolStartEvent.asObservable();

  constructor(private http: HttpClient) { }

  getAll(): Observable<Pools> {
    return this.http.get<Pools>(environment.poolResource, {withCredentials: true});
  }

  emitPoolStartEvent(pool: Pool) {
    this.poolStartEvent.next(pool);
  }
}
