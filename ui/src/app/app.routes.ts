import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { PageNotFoundComponent } from './page-not-found/page-not-found.component';
import { HomeComponent } from './home/home.component';

const routes: Routes = [
    {
        path: '', component: HomeComponent, children: []
    },
    { path: '**', component: PageNotFoundComponent }
];

@NgModule({
    imports: [RouterModule.forRoot(routes, { enableTracing: false })], // <-- debugging purposes only)],
    exports: [RouterModule]
})
export class AppRoutingModule { }

export const routingComponents = [];