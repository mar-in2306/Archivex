import { Component, OnInit, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { EmailTemplateService } from '../../core/services/email-template.service';
import { EmailTemplate } from '../../core/models/document.model';
import { StatusMessageComponent } from '../../shared/components/atoms/status-message/status-message.component';

@Component({
  selector: 'app-email-templates',
  standalone: true,
  imports: [FormsModule, StatusMessageComponent],
  template: `
    <div class="page">
      <header class="page-header"><div><h1>Plantillas de correo</h1><p>Eventos y contenido HTML configurable.</p></div></header>
      <app-status-message [message]="message()" [type]="messageType()" />
      <section class="card form-grid">
        <label>Evento<input [(ngModel)]="form.eventType" placeholder="DOCUMENT_CREATED" /></label>
        <label>Asunto<input [(ngModel)]="form.subject" /></label>
        <label>Activo<select [(ngModel)]="form.isActive"><option [ngValue]="true">Si</option><option [ngValue]="false">No</option></select></label>
        <label class="span-3">HTML<textarea [(ngModel)]="form.bodyHtml"></textarea></label>
        <button class="btn btn-primary" (click)="create()">Crear plantilla</button>
      </section>
      <section class="card">
        <div class="toolbar"><strong>Listado</strong><button class="btn" (click)="load()">Recargar</button></div>
        <div class="table-wrap"><table><thead><tr><th>Evento</th><th>Asunto</th><th>Activa</th><th>Acciones</th></tr></thead><tbody>
          @for (tpl of templates(); track tpl.id) {
            <tr><td><input [(ngModel)]="tpl.eventType" /></td><td><input [(ngModel)]="tpl.subject" /></td><td>{{ tpl.isActive ? 'Si' : 'No' }}</td><td class="actions"><button class="btn" (click)="update(tpl)">Guardar</button><button class="btn btn-danger" (click)="remove(tpl)">Eliminar</button></td></tr>
          } @empty { <tr><td colspan="4" class="empty">Sin plantillas.</td></tr> }
        </tbody></table></div>
      </section>
    </div>
  `,
})
export class EmailTemplatesComponent implements OnInit {
  private api = inject(EmailTemplateService);
  readonly templates = signal<EmailTemplate[]>([]);
  readonly message = signal<string | null>(null);
  readonly messageType = signal<'info' | 'success' | 'error'>('info');
  form: EmailTemplate = { eventType: '', subject: '', bodyHtml: '', isActive: true };

  ngOnInit(): void { this.load(); }
  load(): void { this.api.list().subscribe({ next: (templates) => this.templates.set(templates), error: (err) => this.show(err.message, 'error') }); }
  create(): void { this.api.create(this.form).subscribe({ next: () => { this.form = { eventType: '', subject: '', bodyHtml: '', isActive: true }; this.show('Plantilla creada.', 'success'); this.load(); }, error: (err) => this.show(err.message, 'error') }); }
  update(tpl: EmailTemplate): void { if (!tpl.id) return; this.api.update(tpl.id, tpl).subscribe({ next: () => this.show('Plantilla actualizada.', 'success'), error: (err) => this.show(err.message, 'error') }); }
  remove(tpl: EmailTemplate): void { if (!tpl.id || !confirm('Eliminar plantilla?')) return; this.api.delete(tpl.id).subscribe({ next: () => this.load(), error: (err) => this.show(err.message, 'error') }); }
  private show(text: string, type: 'info' | 'success' | 'error'): void { this.message.set(text); this.messageType.set(type); }
}
