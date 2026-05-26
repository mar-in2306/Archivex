import { Routes } from '@angular/router';
import { authGuard, publicOnlyGuard } from './core/guards/auth.guard';

export const routes: Routes = [
  {
    path: 'auth',
    canActivate: [publicOnlyGuard],
    children: [
      {
        path: 'login',
        loadComponent: () =>
          import('./features/auth/login/login.component').then((m) => m.LoginComponent),
      },
      {
        path: 'register',
        loadComponent: () =>
          import('./features/organizations/organizations.component').then((m) => m.OrganizationsComponent),
      },
      { path: '', redirectTo: 'login', pathMatch: 'full' },
    ],
  },
  {
    path: '',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./shared/components/main-layout/main-layout.component').then(
        (m) => m.MainLayoutComponent,
      ),
    children: [
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
      {
        path: 'dashboard',
        loadComponent: () =>
          import('./features/dashboard/dashboard.component').then(
            (m) => m.DashboardComponent,
          ),
      },
      { path: 'documents', loadComponent: () => import('./features/documents/documents.component').then((m) => m.DocumentsComponent) },
      { path: 'document-types', loadComponent: () => import('./features/document-types/document-types.component').then((m) => m.DocumentTypesComponent) },
      { path: 'users', loadComponent: () => import('./features/users/users.component').then((m) => m.UsersComponent) },
      { path: 'organizations', loadComponent: () => import('./features/organizations/organizations.component').then((m) => m.OrganizationsComponent) },
      { path: 'workflow', loadComponent: () => import('./features/workflow/workflow.component').then((m) => m.WorkflowComponent) },
      { path: 'email-templates', loadComponent: () => import('./features/email-templates/email-templates.component').then((m) => m.EmailTemplatesComponent) },
      { path: 'audit', loadComponent: () => import('./features/audit/audit.component').then((m) => m.AuditComponent) },
    ],
  },
  { path: '**', redirectTo: 'auth/login' },
];
