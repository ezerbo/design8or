import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class RotationService {

  private rotationTimeEvent = new Subject<string>();

  rotationTimeEventBus$ = this.rotationTimeEvent.asObservable();

  constructor() { }

  emitRotationTimeUpdateEvent(rotationTime: string) {
    this.rotationTimeEvent.next(rotationTime);
  }

}