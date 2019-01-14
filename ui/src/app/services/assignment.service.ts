import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';
import { Assignment } from '../app.model';

@Injectable({
  providedIn: 'root'
})
export class AssignmentService {

  private assignmentEvent = new Subject<Assignment>();

  assignmentEventBus$ = this.assignmentEvent.asObservable();

  constructor() { }

  emitAssignmentEvent(assignment: Assignment) {
    this.assignmentEvent.next(assignment);
  }
}