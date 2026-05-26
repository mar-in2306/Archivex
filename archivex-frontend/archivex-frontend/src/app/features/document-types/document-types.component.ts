import { Component, OnInit, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../core/services/auth.service';
import { DocumentTypeService } from '../../core/services/document-type.service';
import { DocumentType } from '../../core/models/document.model';
import { StatusMessageComponent } from '../../shared/components/atoms/status-message/status-message.component';

@Component({
  selector: 'app-document-types',
  standalone: true,
  imports: [FormsModule, StatusMessageComponent],
  template: `
    <div class="page">
      <header class="page-header"><div><h1>Tipos documentales</h1><p>Parametros para clasificar documentos.</p></div></header>
      <app-status-message [message]="message()" [type]="messageType()" />
      <section class="card form-grid">
        <label>Nombre<input [(ngModel)]="form.name" /></label>
        <label>Activo<select [(ngModel)]="form.isActive"><option [ngValue]="true">Si</option><option [ngValue]="false">No</option></select></label>
        <label class="span-3">Descripcion<textarea [(ngModel)]="form.description"></textarea></label>
        <button class="btn btn-primary" (click)="create()">Crear tipo</button>
      </section>
      <section class="card">
        <div class="toolbar"><strong>Listado</strong><button class="btn" (click)="load()">Recargar</button></div>
        <div class="table-wrap"><table><thead><tr><th>Nombre</th><th>Descripcion</th><th>Activo</th><th>Acciones</th></tr></thead><tbody>
          @for (type of types(); track type.id) {
            <tr><td><input [(ngModel)]="type.name" /></td><td><input [(ngModel)]="type.description" /></td><td><select [(ngModel)]="type.isActive"><option [ngValue]="true">Si</option><option [ngValue]="false">No</option></select></td><td class="actions"><button class="btn" (click)="update(type)">Guardar</button><button class="btn btn-danger" (click)="remove(type)">Eliminar</button></td></tr>
          } @empty { <tr><td colspan="4" class="empty">Sin tipos documentales.</td></tr> }
        </tbody></table></div>
      </section>
    </div>
  `,
})
export class DocumentTypesComponent implements OnInit {
  private api = inject(DocumentTypeService);
  private auth = inject(AuthService);
  readonly types = signal<DocumentType[]>([]);
  readonly message = signal<string | null>(null);
  readonly messageType = signal<'info' | 'success' | 'error'>('info');
  form: DocumentType = { name: '', description: '', isActive: true };

  ngOnInit(): void { this.load(); }
  load(): void {
    const orgId = this.orgId();
    if (!orgId) return;
    this.api.listByOrganization(orgId).subscribe({ next: (types) => this.types.set(types), error: (err) => this.show(err.message, 'error') });
  }
  create(): void {
    const orgId = this.orgId();
    if (!orgId) return;
    this.api.create({ ...this.form, organizationId: orgId }).subscribe({ next: () => { this.form = { name: '', description: '', isActive: true }; this.show('Tipo creado.', 'success'); this.load(); }, error: (err) => this.show(err.message, 'error') });
  }
  update(type: DocumentType): void {
    const orgId = this.orgId();
    if (!orgId || !type.id) return;
    this.api.update(type.id, orgId, type).subscribe({ next: () => this.show('Tipo actualizado.', 'success'), error: (err) => this.show(err.message, 'error') });
  }
  remove(type: DocumentType): void {
    const orgId = this.orgId();
    if (!orgId || !type.id || !confirm('Eliminar tipo documental?')) return;
    this.api.delete(type.id, orgId).subscribe({ next: () => this.load(), error: (err) => this.show(err.message, 'error') });
  }
  private orgId(): number | undefined { return this.auth.currentUser()?.organizationId; }
  private show(text: string, type: 'info' | 'success' | 'error'): void { this.message.set(text); this.messageType.set(type); }
}
