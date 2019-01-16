declare var $: any;
import { Component, OnInit } from '@angular/core';
import { PoolService } from '../services/pool.service';
import { Pools, User, Pool } from '../app.model';
import { UserService } from '../services/user.service';
import { AssignmentService } from '../services/assignment.service';

@Component({
  selector: 'app-pool',
  templateUrl: './pool.component.html',
  styleUrls: ['./pool.component.css']
})
export class PoolComponent implements OnInit {

  pools: Pools

  users: User[] = [];

  constructor(
    private userService: UserService,
    private poolService: PoolService,
    private assignmentService: AssignmentService,
  ) { $('#poolProgress').progress(); }

  ngOnInit() {
    this.poolService.getAll()
      .subscribe(pools => { this.pools = pools });

    this.assignmentService.assignmentEventBus$.subscribe(() => {
      this.pools.currentPoolParticipantsCount += 1;
      this.calculateProgress();
    });

    this.userService.userAdditionEventBus$.subscribe(user => { this.onUserAddition(user) });

    this.userService.userDeletionEventBus$.subscribe(user => { this.onUserDeletion(user) });

    this.poolService.poolStartEventBus$.subscribe((pool) => { this.onPoolStart(pool) })

    //TODO add user count to pools object
    this.userService.getAll().subscribe(users => { this.users = users });
  }

  calculateProgress() {
    this.pools.currentPoolProgress = Math.floor((100 * this.pools.currentPoolParticipantsCount) / this.users.length);
  }

  onUserAddition(user: User) {
    this.users.push(user);
    this.calculateProgress();
  }

  onUserDeletion(user: User) {
    this.users = this.users.filter((u) => u.id != user.id);
    this.calculateProgress();
  }

  onPoolStart(pool: Pool) {//When a new pool starts
    this.pools.currentPoolParticipantsCount = 0;
    this.pools.current = pool;
  }

}