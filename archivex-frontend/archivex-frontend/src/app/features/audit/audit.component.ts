import { Component, OnInit, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { AuditService } from '../../core/services/audit.service';
import { AuditLog } from '../../core/models/document.model';
import { StatusMessageComponent } from '../../shared/components/atoms/status-message/status-message.component';

@Component({
  selector: 'app-audit',
  standalone: true,
  imports: [FormsModule, StatusMessageComponent],
  template: `
    <div class="page">
      <header class="page-header"><div><h1>Auditoria</h1><p>Historial por organizacion, documento o usuario.</p></div></header>
      <app-status-message [message]="message()" type="error" />
      <section class="card toolbar">
        <button class="btn btn-primary" (click)="loadOrganization()">Organizacion</button>
        <label>Documento ID<input type="number" [(ngModel)]="documentId" /></label>
        <button class="btn" (click)="loadDocument()">Buscar documento</button>
        <label>Usuario ID<input type="number" [(ngModel)]="userId" /></label>
        <button class="btn" (click)="loadUser()">Buscar usuario</button>
      </section>
      <section class="card">
        <div class="table-wrap"><table><thead><tr><th>Fecha</th><th>Accion</th><th>Documento</th><th>Usuario</th><th>Detalle</th></tr></thead><tbody>
          @for (log of logs(); track log.id) {
            <tr><td>{{ log.performedAt ?? '-' }}</td><td>{{ log.action }}</td><td>{{ log.documentTitle ?? log.documentId ?? '-' }}</td><td>{{ log.performedByName ?? log.performedById ?? '-' }}</td><td>{{ log.details ?? '-' }}</td></tr>
          } @empty { <tr><td colspan="5" class="empty">Sin registros.</td></tr> }
        </tbody></table></div>
      </section>
    </div>
  `,
})
export class AuditComponent implements OnInit {
  private api = inject(AuditService);
  readonly logs = signal<AuditLog[]>([]);
  readonly message = signal<string | null>(null);
  documentId = 0;
  userId = 0;
  ngOnInit(): void { this.loadOrganization(); }
  loadOrganization(): void { this.api.byOrganization().subscribe({ next: (logs) => this.logs.set(logs), error: (err) => this.message.set(err.message) }); }
  loadDocument(): void { if (this.documentId) this.api.byDocument(this.documentId).subscribe({ next: (logs) => this.logs.set(logs), error: (err) => this.message.set(err.message) }); }
  loadUser(): void { if (this.userId) this.api.byUser(this.userId).subscribe({ next: (logs) => this.logs.set(logs), error: (err) => this.message.set(err.message) }); }
}
