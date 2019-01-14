import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, Subject } from 'rxjs';
import { User } from '../app.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  private userAdditionEvent = new Subject<User>();

  userAdditionEventBus$ = this.userAdditionEvent.asObservable();

  private userDeletionEvent = new Subject<User>();

  userDeletionEventBus$ = this.userDeletionEvent.asObservable();

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

  getDesignated(): Observable<User> {
    return this.http.get<User>(`${environment.userResource}/designated`, { withCredentials: true });
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

  emitUserAddtionEvent(user: User) {
    this.userAdditionEvent.next(user);
  }

  emitUserDeletiontionEvent(user: User) {
    this.userDeletionEvent.next(user);
  }

}