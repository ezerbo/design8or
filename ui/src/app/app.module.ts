import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { AmazingTimePickerModule } from 'amazing-time-picker';

import { AppComponent } from './app.component';
import { HttpClientModule } from '@angular/common/http';
import { PageNotFoundComponent } from './page-not-found/page-not-found.component';
import { HomeComponent } from './home/home.component';
import { AppRoutingModule } from './app.routes';
import { DatePipe } from '@angular/common';
import { LeadComponent } from './lead/lead.component';
import { PoolComponent } from './pool/pool.component';
import { RotationComponent } from './rotation/rotation.component';
import { MessageComponent } from './message/message.component';
import { UserComponent } from './user/user.component';
import { ServiceWorkerModule } from '@angular/service-worker';
import { environment } from '../environments/environment';

@NgModule({
   declarations: [
      AppComponent,
      PageNotFoundComponent,
      HomeComponent,
      LeadComponent,
      PoolComponent,
      RotationComponent,
      MessageComponent,
      UserComponent
   ],
   imports: [
      FormsModule,
      BrowserModule,
      AppRoutingModule,
      HttpClientModule,
      AmazingTimePickerModule,
      ReactiveFormsModule,
      ServiceWorkerModule.register('/ngsw-worker.js', { enabled: environment.production })
   ],
   providers: [
      DatePipe
   ],
   bootstrap: [
      AppComponent
   ]
})
export class AppModule { }
