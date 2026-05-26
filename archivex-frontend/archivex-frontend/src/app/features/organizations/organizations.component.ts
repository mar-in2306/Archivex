import { Component, OnInit, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { OrganizationService } from '../../core/services/organization.service';
import { Organization } from '../../core/models/organization.model';
import { StatusMessageComponent } from '../../shared/components/atoms/status-message/status-message.component';

@Component({
  selector: 'app-organizations',
  standalone: true,
  imports: [FormsModule, StatusMessageComponent],
  template: `
    <div class="page">
      <header class="page-header"><div><h1>Organizaciones</h1><p>Registro publico y CRUD administrativo.</p></div></header>
      <app-status-message [message]="message()" [type]="messageType()" />

      <section class="card form-grid">
        <label>Nombre org<input [(ngModel)]="register.orgName" /></label>
        <label>Dominio<input [(ngModel)]="register.orgDomain" /></label>
        <label>Email contacto<input [(ngModel)]="register.orgContactEmail" /></label>
        <label>Admin nombre<input [(ngModel)]="register.adminName" /></label>
        <label>Admin email<input [(ngModel)]="register.adminEmail" /></label>
        <label>Admin password<input type="password" [(ngModel)]="register.adminPassword" /></label>
        <label class="span-3">Descripcion<textarea [(ngModel)]="register.orgDescription"></textarea></label>
        <button class="btn btn-primary" (click)="registerOrg()">Registrar organizacion + admin</button>
      </section>

      <section class="card">
        <div class="toolbar"><strong>Organizaciones</strong><button class="btn" (click)="load()">Recargar</button></div>
        <div class="table-wrap">
          <table>
            <thead><tr><th>Nombre</th><th>Dominio</th><th>Email</th><th>Activa</th><th>Acciones</th></tr></thead>
            <tbody>
              @for (org of organizations(); track org.id) {
                <tr>
                  <td><input [(ngModel)]="org.name" /></td>
                  <td><input [(ngModel)]="org.domain" /></td>
                  <td><input [(ngModel)]="org.contactEmail" /></td>
                  <td>{{ org.isActive ? 'Si' : 'No' }}</td>
                  <td class="actions"><button class="btn" (click)="update(org)">Guardar</button><button class="btn btn-danger" (click)="remove(org)">Eliminar</button></td>
                </tr>
              } @empty {
                <tr><td colspan="5" class="empty">Sin organizaciones o sin permiso ADMIN para listarlas.</td></tr>
              }
            </tbody>
          </table>
        </div>
      </section>
    </div>
  `,
})
export class OrganizationsComponent implements OnInit {
  private api = inject(OrganizationService);
  readonly organizations = signal<Organization[]>([]);
  readonly message = signal<string | null>(null);
  readonly messageType = signal<'info' | 'success' | 'error'>('info');
  register = { orgName: '', orgDomain: '', orgContactEmail: '', orgDescription: '', adminName: '', adminEmail: '', adminPassword: '' };

  ngOnInit(): void { this.load(); }

  load(): void {
    this.api.list().subscribe({ next: (orgs) => this.organizations.set(orgs), error: () => this.organizations.set([]) });
  }

  registerOrg(): void {
    this.api.register(this.register).subscribe({ next: () => { this.show('Organizacion registrada. Ya puedes iniciar sesion con el admin.', 'success'); this.load(); }, error: (err) => this.show(err.message, 'error') });
  }

  update(org: Organization): void {
    if (!org.id) return;
    this.api.update(org.id, org).subscribe({ next: () => this.show('Organizacion actualizada.', 'success'), error: (err) => this.show(err.message, 'error') });
  }

  remove(org: Organization): void {
    if (!org.id || !confirm('Eliminar organizacion?')) return;
    this.api.delete(org.id).subscribe({ next: () => this.load(), error: (err) => this.show(err.message, 'error') });
  }

  private show(text: string, type: 'info' | 'success' | 'error'): void {
    this.message.set(text); this.messageType.set(type);
  }
}
