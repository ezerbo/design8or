import { Component, OnInit } from '@angular/core';
import { User } from '../app.model';
import { UserService } from '../services/user.service';
import { MessageService } from '../services/message.service';

@Component({
  selector: 'app-lead',
  templateUrl: './lead.component.html',
  styleUrls: ['./lead.component.css']
})
export class LeadComponent implements OnInit {

  lead: User;

  constructor(private userService: UserService, private msg: MessageService) { }

  ngOnInit() {
    this.userService.getLead()
      .subscribe(lead => { this.lead = lead });
    this.msg.designationEventBus$.subscribe(lead => { this.lead = lead });
  }

}
