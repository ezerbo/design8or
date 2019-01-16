import { Component, OnInit } from '@angular/core';
import { User } from '../app.model';
import { UserService } from '../services/user.service';
import { AssignmentService } from '../services/assignment.service';

@Component({
  selector: 'app-lead-user',
  templateUrl: './lead-user.component.html',
  styleUrls: ['./lead-user.component.css']
})
export class LeadUserComponent implements OnInit {

  lead: User;

  constructor(
    private userService: UserService,
    private assignmentService: AssignmentService) { }

  ngOnInit() {
    this.userService.getLead()
      .subscribe(lead => { this.lead = lead });
    
    this.assignmentService.assignmentEventBus$
      .subscribe(assignment => { this.lead = assignment.user });
  }

}
