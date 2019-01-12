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


  private designationEvent = new Subject<User>();

  designationEventBus$ = this.designationEvent.asObservable();


  private userAdditionEvent = new Subject<User>();

  userAdditionEventBus$ = this.userAdditionEvent.asObservable();


  private userDeletionEvent = new Subject<User>();

  userDeletionEventBus$ = this.userDeletionEvent.asObservable();
  

  private poolStartEvent = new Subject<Pool>();

  poolStartEventBus$ = this.poolStartEvent.asObservable();

  constructor() { }

  emitSuccessEvent(message: string) {
    this.successMessageEvent.next(message);
  }

  emitErrorEvent(error: Error): void {
    this.errorMessageEvent.next(error);
  }

  emitDesignationEvent(user: User) {
    this.designationEvent.next(user);
  }

  emitUserAddtionEvent(user: User) {
    this.userAdditionEvent.next(user);
  }

  emitUserDeletiontionEvent(user: User) {
    this.userDeletionEvent.next(user);
  }

  emitPoolStartEvent(pool: Pool) {
    this.poolStartEvent.next(pool);
  }

}