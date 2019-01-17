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
  usersCount: number;

  constructor(
    private userService: UserService,
    private poolService: PoolService,
    private assignmentService: AssignmentService) {}

  ngOnInit() {
    $('#poolProgress').progress();
    this.poolService.getAll().subscribe(pools => { this.pools = pools });
    this.assignmentService.assignmentEventBus$.subscribe(() => {
      this.pools.currentPoolParticipantsCount += 1;
      this.calculateProgress();
    });
    this.userService.userAdditionEventBus$.subscribe(() => this.onUserAddition());
    this.userService.userDeletionEventBus$.subscribe(() => this.onUserDeletion());
    this.poolService.poolStartEventBus$.subscribe((pool) => this.onPoolStart(pool))
    this.userService.getAll().subscribe(users => { this.usersCount = users.length });//TODO add user count to pools object
  }

  calculateProgress() {
    let progress = (100 * this.pools.currentPoolParticipantsCount) / this.usersCount;
    this.pools.currentPoolProgress = Math.floor(progress);
  }

  onUserAddition() {
    this.usersCount ++;
    this.calculateProgress();
  }

  onUserDeletion() {
    this.usersCount --;
    this.calculateProgress();
  }

  onPoolStart(pool: Pool) {//When a new pool starts
    this.pools.currentPoolParticipantsCount = 0;
    this.pools.current = pool;
  }

}