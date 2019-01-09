import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { AmazingTimePickerModule } from 'amazing-time-picker';

import { AppComponent } from './app.component';
import { HttpClientModule } from '@angular/common/http';
import { PageNotFoundComponent } from './page-not-found/page-not-found.component';
import { HomeComponent } from './home/home.component';
import { HeaderComponent } from './header/header.component';
import { FooterComponent } from './footer/footer.component';
import { AppRoutingModule } from './app.routes';
import { DatePipe } from '@angular/common';

@NgModule({
    declarations: [
        AppComponent,
        PageNotFoundComponent,
        HomeComponent,
        HeaderComponent,
        FooterComponent
    ],
    imports: [
        FormsModule,
        BrowserModule,
        AppRoutingModule,
        HttpClientModule,
        AmazingTimePickerModule,
        ReactiveFormsModule
    ],
    providers: [DatePipe],
    bootstrap: [
        AppComponent
    ]
})
export class AppModule { }
