import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';
import { adminGuard } from './core/guards/admin.guard';
import { MainLayoutComponent } from './shared/layouts/main-layout/main-layout.component';
import { AuthLayoutComponent } from './shared/layouts/auth-layout/auth-layout.component';

export const routes: Routes = [
  {
    path: 'auth',
    component: AuthLayoutComponent,
    children: [
      { path: 'login', loadComponent: () => import('./features/auth/login/login.component').then(m => m.LoginComponent) },
      { path: 'register', loadComponent: () => import('./features/auth/register/register.component').then(m => m.RegisterComponent) },
      { path: '', redirectTo: 'login', pathMatch: 'full' },
    ],
  },
  {
    path: 'books',
    component: MainLayoutComponent,
    canActivate: [authGuard],
    children: [
      { path: '', loadComponent: () => import('./features/books/book-list/book-list.component').then(m => m.BookListComponent) },
      { path: 'new', loadComponent: () => import('./features/books/book-form/book-form.component').then(m => m.BookFormComponent), canActivate: [adminGuard] },
      { path: ':id', loadComponent: () => import('./features/books/book-detail/book-detail.component').then(m => m.BookDetailComponent) },
      { path: ':id/edit', loadComponent: () => import('./features/books/book-form/book-form.component').then(m => m.BookFormComponent), canActivate: [adminGuard] },
    ],
  },
  {
    path: 'loans',
    component: MainLayoutComponent,
    canActivate: [authGuard],
    children: [
      { path: '', loadComponent: () => import('./features/loans/loan-list/loan-list.component').then(m => m.LoanListComponent) },
      { path: 'scan', loadComponent: () => import('./features/loans/loan-scan/loan-scan.component').then(m => m.LoanScanComponent) },
    ],
  },
  {
    path: 'users',
    component: MainLayoutComponent,
    canActivate: [authGuard, adminGuard],
    loadComponent: () => import('./features/users/user-list/user-list.component').then(m => m.UserListComponent),
  },
  { path: '', redirectTo: '/books', pathMatch: 'full' },
  { path: '**', redirectTo: '/books' },
];
