import { Component, OnInit, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../core/services/auth.service';
import { UserService } from '../../core/services/user.service';
import { User } from '../../core/models/user.model';
import { UserRole } from '../../core/models/role.model';
import { StatusMessageComponent } from '../../shared/components/atoms/status-message/status-message.component';

@Component({
  selector: 'app-users',
  standalone: true,
  imports: [FormsModule, StatusMessageComponent],
  template: `
    <div class="page">
      <header class="page-header"><div><h1>Usuarios</h1><p>Usuarios de la organizacion actual.</p></div></header>
      <app-status-message [message]="message()" [type]="messageType()" />

      <section class="card form-grid">
        <label>Nombre<input [(ngModel)]="form.name" /></label>
        <label>Email<input type="email" [(ngModel)]="form.email" /></label>
        <label>Contrasena<input type="password" [(ngModel)]="form.password" /></label>
        <label>Rol<select [(ngModel)]="form.role"><option value="ADMIN">ADMIN</option><option value="USER">USER</option></select></label>
        <button class="btn btn-primary" (click)="create()">Crear usuario</button>
      </section>

      <section class="card">
        <div class="toolbar"><strong>Listado</strong><button class="btn" (click)="load()">Recargar</button></div>
        <div class="table-wrap">
          <table>
            <thead><tr><th>Nombre</th><th>Email</th><th>Rol</th><th>Activo</th><th>Acciones</th></tr></thead>
            <tbody>
              @for (user of users(); track user.id) {
                <tr>
                  <td><input [(ngModel)]="user.name" /></td>
                  <td>{{ user.email }}</td>
                  <td><select [(ngModel)]="user.role"><option value="ADMIN">ADMIN</option><option value="USER">USER</option></select></td>
                  <td>{{ user.isActive ? 'Si' : 'No' }}</td>
                  <td class="actions">
                    <button class="btn" (click)="update(user)">Guardar</button>
                    <button class="btn" (click)="toggle(user)">Activar/Inactivar</button>
                    <button class="btn btn-danger" (click)="remove(user)">Eliminar</button>
                  </td>
                </tr>
              } @empty {
                <tr><td colspan="5" class="empty">No hay usuarios.</td></tr>
              }
            </tbody>
          </table>
        </div>
      </section>
    </div>
  `,
})
export class UsersComponent implements OnInit {
  private api = inject(UserService);
  private auth = inject(AuthService);
  readonly users = signal<User[]>([]);
  readonly message = signal<string | null>(null);
  readonly messageType = signal<'info' | 'success' | 'error'>('info');
  form: User = { name: '', email: '', password: '', role: UserRole.USER, isActive: true };

  ngOnInit(): void { this.load(); }

  load(): void {
    const orgId = this.auth.currentUser()?.organizationId;
    if (!orgId) return this.show('No hay organizacion en la sesion.', 'error');
    this.api.listByOrganization(orgId).subscribe({ next: (users) => this.users.set(users), error: (err) => this.show(err.message, 'error') });
  }

  create(): void {
    const orgId = this.auth.currentUser()?.organizationId;
    if (!orgId) return;
    this.api.create({ ...this.form, organizationId: orgId }).subscribe({
      next: () => { this.form = { name: '', email: '', password: '', role: UserRole.USER, isActive: true }; this.show('Usuario creado.', 'success'); this.load(); },
      error: (err) => this.show(err.message, 'error'),
    });
  }

  update(user: User): void {
    if (!user.id) return;
    this.api.update(user.id, { name: user.name, role: user.role, isActive: user.isActive }).subscribe({ next: () => this.show('Usuario actualizado.', 'success'), error: (err) => this.show(err.message, 'error') });
  }

  toggle(user: User): void {
    if (!user.id) return;
    this.api.toggleStatus(user.id).subscribe({ next: () => this.load(), error: (err) => this.show(err.message, 'error') });
  }

  remove(user: User): void {
    if (!user.id || !confirm('Eliminar usuario?')) return;
    this.api.delete(user.id).subscribe({ next: () => this.load(), error: (err) => this.show(err.message, 'error') });
  }

  private show(text: string, type: 'info' | 'success' | 'error'): void {
    this.message.set(text); this.messageType.set(type);
  }
}
