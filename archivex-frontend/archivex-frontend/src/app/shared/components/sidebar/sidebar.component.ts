import { Component, inject } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

interface NavItem {
  label: string;
  route: string;
}

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [RouterLink, RouterLinkActive],
  template: `
    <aside class="sidebar">
      <div class="sidebar__brand">
        <div class="sidebar__logo">A</div>
        <span class="sidebar__name">Archivex</span>
      </div>

      <nav class="sidebar__section">
        @for (item of mainNav; track item.route) {
          <a [routerLink]="item.route" routerLinkActive="sidebar__item--active" class="sidebar__item">{{ item.label }}</a>
        }
      </nav>

      @if (currentUser(); as user) {
        <div class="sidebar__user">
          <div class="sidebar__avatar">{{ initials(user.name) }}</div>
          <div class="sidebar__user-info">
            <div class="sidebar__user-name">{{ user.name }}</div>
            <div class="sidebar__user-role">{{ user.role }}</div>
          </div>
          <button class="sidebar__logout" (click)="logout()">Salir</button>
        </div>
      }
    </aside>
  `,
  styles: [`
    .sidebar { width: var(--sidebar-width); height: 100vh; background: var(--color-bg-secondary); border-right: 1px solid var(--color-border-tertiary); padding: 16px 12px; display: flex; flex-direction: column; gap: 8px; position: sticky; top: 0; }
    .sidebar__brand { display: flex; align-items: center; gap: 8px; padding: 6px 8px 14px; border-bottom: 1px solid var(--color-border-tertiary); margin-bottom: 6px; }
    .sidebar__logo { width: 24px; height: 24px; border-radius: 6px; background: var(--color-info-bg); color: var(--color-info-text); display: flex; align-items: center; justify-content: center; font-weight: 700; }
    .sidebar__name { font-size: var(--fs-md); font-weight: 600; }
    .sidebar__section { display: flex; flex-direction: column; gap: 2px; }
    .sidebar__item { padding: 8px 9px; border-radius: var(--radius-sm); font-size: var(--fs-base); color: var(--color-text-secondary); text-decoration: none; }
    .sidebar__item:hover { background: var(--color-bg-tertiary); text-decoration: none; }
    .sidebar__item--active { background: var(--color-info-bg); color: var(--color-info-text); font-weight: 600; }
    .sidebar__user { margin-top: auto; padding: 10px 4px 0; border-top: 1px solid var(--color-border-tertiary); display: flex; align-items: center; gap: 8px; }
    .sidebar__avatar { width: 28px; height: 28px; border-radius: 50%; background: var(--color-info-bg); color: var(--color-info-text); display: flex; align-items: center; justify-content: center; font-size: var(--fs-xs); font-weight: 600; }
    .sidebar__user-info { min-width: 0; flex: 1; font-size: var(--fs-sm); line-height: 1.25; }
    .sidebar__user-name { font-weight: 600; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
    .sidebar__user-role { color: var(--color-text-tertiary); font-size: var(--fs-xs); }
    .sidebar__logout { border: 0; background: transparent; color: var(--color-info-text); font-size: var(--fs-xs); }
  `],
})
export class SidebarComponent {
  private auth = inject(AuthService);
  readonly currentUser = this.auth.currentUser;

  readonly mainNav: NavItem[] = [
    { label: 'Dashboard', route: '/dashboard' },
    { label: 'Documentos', route: '/documents' },
    { label: 'Tipos documentales', route: '/document-types' },
    { label: 'Workflow', route: '/workflow' },
    { label: 'Usuarios', route: '/users' },
    { label: 'Organizaciones', route: '/organizations' },
    { label: 'Plantillas correo', route: '/email-templates' },
    { label: 'Auditoria', route: '/audit' },
  ];

  initials(name: string): string {
    return name.split(' ').map((p) => p[0]).join('').slice(0, 2).toUpperCase();
  }

  logout(): void {
    this.auth.logout();
    location.href = '/auth/login';
  }
}
