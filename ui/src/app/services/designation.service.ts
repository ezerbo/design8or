import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { User, Designation, DesignationResponse } from '../app.model';
import { environment } from '../../environments/environment';
import { Observable, Subject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class DesignationService {

  private designationEvent = new Subject<Designation>();

  designationEventBus$ = this.designationEvent.asObservable();

  constructor(private http: HttpClient) { }

  designate(user: User): Observable<User> {
    return this.http.post<User>(environment.designationResource, user, { withCredentials: true });
  }

  getCurrent(): Observable<Designation> {
    return this.http.get<Designation>(`${environment.designationResource}/current`, { withCredentials: true });
  }

  processDesignationResponse(response: DesignationResponse): Observable<Designation> {
    return this.http.post<Designation>(`${environment.designationResource}/response`, response, { withCredentials: true });
  }

  emitDesignationEvent(designation: Designation) {
    this.designationEvent.next(designation);
  }

}
