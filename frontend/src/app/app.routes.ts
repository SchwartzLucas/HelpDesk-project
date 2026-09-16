import { Routes } from '@angular/router';
import { Login } from './pages/login/login';
import { AppLayout } from './layout/app-layout';
import { authGuard } from './core/guards/auth.guard';
import { Dashboard } from './pages/dashboard/dashboard';
import { DemandsPage } from './pages/demands/demands';
import { TeamsPage } from './pages/teams/teams';
import { ClientsPage } from './pages/clients/clients';
import { UsersPage } from './pages/users/users';

export const routes: Routes = [
  { path: 'login', component: Login },
  {
    path: '',
    component: AppLayout,
    canActivate: [authGuard],
    children: [
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
      { path: 'dashboard', component: Dashboard },
      { path: 'demandas', component: DemandsPage, data: { mine: true } },
      { path: 'demandas/todas', component: DemandsPage, data: { mine: false } },
      { path: 'times', component: TeamsPage },
      { path: 'clientes', component: ClientsPage },
      { path: 'usuarios', component: UsersPage },
    ],
  },
  { path: '**', redirectTo: 'dashboard' },
];