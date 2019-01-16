import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, Subject } from 'rxjs';
import { Parameter } from '../app.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ParameterService {

  private parametersUpdateEvent = new Subject<Parameter>();

  parametersUpdateEventBus$ = this.parametersUpdateEvent.asObservable();

  constructor(private http: HttpClient) { }

  get(): Observable<Parameter> {
    return this.http.get<Parameter>(environment.parameterResource, { withCredentials: true });
  }

  update(parameter: Parameter): Observable<Parameter> {
    return this.http.put<Parameter>(environment.parameterResource, parameter, { withCredentials: true });
  }

  emitParametersUpdateEvent(paramter: Parameter) {
    this.parametersUpdateEvent.next(paramter);
  }
}