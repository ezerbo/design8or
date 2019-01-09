import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Pools } from '../app.model';
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
