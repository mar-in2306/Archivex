import { Component, OnInit, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { DocumentService } from '../../core/services/document.service';
import { WorkflowService } from '../../core/services/workflow.service';
import { Document, DocumentWorkflow } from '../../core/models/document.model';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [RouterLink],
  template: `
    <div class="page">
      <header class="page-header">
        <div>
          <h1>Panel principal</h1>
          <p>Hola{{ userName() ? ', ' + userName() : '' }}. Resumen minimo de Archivex.</p>
        </div>
        <a class="btn btn-primary" routerLink="/documents">Subir documento</a>
      </header>

      <section class="metrics">
        <div class="metric"><span>Documentos</span><strong>{{ documents().length }}</strong></div>
        <div class="metric"><span>Pendientes</span><strong>{{ pendingTasks().length }}</strong></div>
        <div class="metric"><span>Aprobados</span><strong>{{ approvedCount() }}</strong></div>
      </section>

      <section class="card">
        <h2>Documentos recientes</h2>
        <div class="list">
          @for (doc of documents().slice(0, 5); track doc.id) {
            <div class="row">
              <span>{{ doc.title }}</span>
              <small>{{ doc.status ?? 'CREATED' }}</small>
            </div>
          } @empty {
            <p class="muted">Todavia no hay documentos cargados.</p>
          }
        </div>
      </section>
    </div>
  `,
})
export class DashboardComponent implements OnInit {
  private auth = inject(AuthService);
  private documentsApi = inject(DocumentService);
  private workflowApi = inject(WorkflowService);

  readonly documents = signal<Document[]>([]);
  readonly pendingTasks = signal<DocumentWorkflow[]>([]);

  ngOnInit(): void {
    this.documentsApi.list().subscribe({ next: (docs) => this.documents.set(docs), error: () => this.documents.set([]) });
    this.workflowApi.listPendingTasks().subscribe({ next: (tasks) => this.pendingTasks.set(tasks), error: () => this.pendingTasks.set([]) });
  }

  userName(): string {
    return this.auth.currentUser()?.name?.split(' ')[0] ?? '';
  }

  approvedCount(): number {
    return this.documents().filter((doc) => doc.status === 'APPROVED').length;
  }
}
