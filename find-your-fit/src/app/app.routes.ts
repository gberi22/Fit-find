import { Routes } from '@angular/router';
import { authGuard } from '@auth/auth.guard';

export const routes: Routes = [
  {
    path: '',
    pathMatch: 'full',
    loadComponent: () => import('@pages/home/home.component').then((m) => m.HomeComponent),
  },
  {
    path: 'login',
    loadComponent: () => import('@pages/login/login.component').then((m) => m.LoginComponent),
  },
  {
    path: 'register',
    loadComponent: () =>
      import('@pages/register/register.component').then((m) => m.RegisterComponent),
  },
  {
    path: 'generate',
    canActivate: [authGuard],
    loadComponent: () =>
      import('@pages/generate/generate.component').then((m) => m.GenerateComponent),
  },
  {
    path: 'results',
    canActivate: [authGuard],
    loadComponent: () => import('@pages/results/results.component').then((m) => m.ResultsComponent),
  },
  {
    path: 'profile',
    canActivate: [authGuard],
    loadComponent: () => import('@pages/profile/profile.component').then((m) => m.ProfileComponent),
  },
  { path: '**', redirectTo: '' },
];
