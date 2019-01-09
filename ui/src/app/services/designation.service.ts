import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { User } from '../app.model';
import { environment } from '../../environments/environment';
import { Observable } from '../../../node_modules/rxjs';

@Injectable({
  providedIn: 'root'
})
export class DesignationService {

  constructor(private http: HttpClient) { }

  designate(user: User): Observable<User> {
    return this.http.post<User>(environment.designationResource, user, { withCredentials: true });
  }
}
