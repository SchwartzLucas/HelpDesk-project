import { Routes } from '@angular/router';
import { Login } from './pages/login/login';
import {Demand} from './pages/demand/demand';

export const routes: Routes = [
  {
    path: '',
    redirectTo: 'login',
    pathMatch: 'full'
  },
  {
    path: 'login',
    component: Login
  },
  {
    path: 'demands',
    component: Demand
  },
  {
    path: '**',
    redirectTo: 'login'
  }
];
