import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';
import { Error, User, Pool } from '../app.model';
import { HttpErrorResponse } from '@angular/common/http';

export function processErrorResponse(errorResponse: HttpErrorResponse) {
  return { description: errorResponse.error.description, message: errorResponse.error.message };
}

@Injectable({
  providedIn: 'root'
})
export class MessageService {

  private successMessageEvent = new Subject<string>();

  successMessageEventBus$ = this.successMessageEvent.asObservable();

  private errorMessageEvent = new Subject<Error>();

  errorMessageEventBus$ = this.errorMessageEvent.asObservable();

  constructor() { }

  emitSuccessEvent(message: string) {
    this.successMessageEvent.next(message);
  }

  emitErrorEvent(error: Error): void {
    this.errorMessageEvent.next(error);
  }
  
}