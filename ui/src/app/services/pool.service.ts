import { Injectable } from '@angular/core';
import { HttpClient } from '../../../node_modules/@angular/common/http';
import { Observable } from '../../../node_modules/rxjs';
import { Pool, Pools } from '../app.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class PoolService {

  constructor(private http: HttpClient) { }

  getAll(): Observable<Pools> {
    return this.http.get<Pools>(environment.poolResource, {withCredentials: true});
  }
}
