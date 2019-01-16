import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { AmazingTimePickerModule } from 'amazing-time-picker';

import { NgxSpinnerModule } from 'ngx-spinner';
import { AppComponent } from './app.component';
import { HttpClientModule } from '@angular/common/http';
import { PageNotFoundComponent } from './page-not-found/page-not-found.component';
import { HomeComponent } from './home/home.component';
import { AppRoutingModule } from './app.routes';
import { DatePipe } from '@angular/common';
import { LeadUserComponent } from './lead-user/lead-user.component';
import { PoolComponent } from './pool/pool.component';
import { RotationComponent } from './rotation/rotation.component';
import { MessageComponent } from './message/message.component';
import { UserComponent } from './user/user.component';
import { ServiceWorkerModule } from '@angular/service-worker';
import { environment } from '../environments/environment';
import { ParameterComponent } from './parameter/parameter.component';
import { DesignationCountdownComponent } from './designation-countdown/designation-countdown.component';
import { DesignatedUserComponent } from './designated-user/designated-user.component';
import { DesignationResponseComponent } from './designation-response/designation-response.component';

@NgModule({
   declarations: [
      AppComponent,
      PageNotFoundComponent,
      HomeComponent,
      LeadUserComponent,
      PoolComponent,
      RotationComponent,
      MessageComponent,
      UserComponent,
      ParameterComponent,
      DesignationCountdownComponent,
      DesignatedUserComponent,
      DesignationResponseComponent
   ],
   imports: [
      FormsModule,
      BrowserModule,
      AppRoutingModule,
      HttpClientModule,
      NgxSpinnerModule,
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
