import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { User } from '../app.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  constructor(private http: HttpClient) { }

  getAll(): Observable<User[]> {
    return this.http.get<User[]>(environment.userResource, { withCredentials: true });
  }

  getCandidates(): Observable<User[]> {
    return this.http.get<User[]>(`${environment.userResource}/candidates`, { withCredentials: true });
  }

  getLead(): Observable<User> {
    return this.http.get<User>(`${environment.userResource}/lead`, { withCredentials: true });
  }

  create(user: User): Observable<User> {
    return this.http.post<User>(environment.userResource, user, { withCredentials: true });
  }

  update(user: User): Observable<User> {
    return this.http.put<User>(environment.userResource, user, { withCredentials: true });
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${environment.userResource}/${id}`, { withCredentials: true });
  }

}