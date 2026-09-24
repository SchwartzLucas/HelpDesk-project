import { Routes } from '@angular/router';
import { Login } from './pages/login/login';
import { DemandCreateLayout } from './components/demands/demand-create-layout/demand-create-layout';
import { DemandEditLayout } from './components/demands/demand-edit-layout/demand-edit-layout';
import { DemandListLayoutComponent } from './components/demands/demand-list-layout/demand-list-layout';

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
    children: [
      {
        path: '',
        component: DemandListLayoutComponent
      },
      {
        path: 'create',
        component: DemandCreateLayout
      },
      {
        path: `:publicId`,
        component: DemandEditLayout
      },
    ]
  },

  {
    path: '**',
    redirectTo: 'login'
  }
];
