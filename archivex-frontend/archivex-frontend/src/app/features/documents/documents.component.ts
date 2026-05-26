import { Component, OnInit, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { DocumentService } from '../../core/services/document.service';
import { DocumentTypeService } from '../../core/services/document-type.service';
import { AuthService } from '../../core/services/auth.service';
import { Document, DocumentStatus, DocumentType } from '../../core/models/document.model';
import { StatusMessageComponent } from '../../shared/components/atoms/status-message/status-message.component';

@Component({
  selector: 'app-documents',
  standalone: true,
  imports: [FormsModule, StatusMessageComponent],
  template: `
    <div class="page">
      <header class="page-header">
        <div><h1>Documentos</h1><p>Crear, listar, actualizar estado, descargar y eliminar.</p></div>
      </header>

      <app-status-message [message]="message()" [type]="messageType()" />

      <section class="card form-grid">
        <label>Titulo<input [(ngModel)]="form.title" /></label>
        <label>Tipo documental
          <select [(ngModel)]="form.documentTypeId">
            <option [ngValue]="undefined">Sin tipo</option>
            @for (type of types(); track type.id) {
              <option [ngValue]="type.id">{{ type.name }}</option>
            }
          </select>
        </label>
        <label>Archivo<input type="file" (change)="pickFile($event)" /></label>
        <label class="span-3">Descripcion<textarea [(ngModel)]="form.description"></textarea></label>
        <button class="btn btn-primary" (click)="save()">Guardar documento</button>
      </section>

      <section class="card">
        <div class="toolbar">
          <strong>Listado</strong>
          <button class="btn" (click)="load()">Recargar</button>
        </div>
        <div class="table-wrap">
          <table>
            <thead><tr><th>Titulo</th><th>Tipo</th><th>Estado</th><th>Archivo</th><th>Acciones</th></tr></thead>
            <tbody>
              @for (doc of documents(); track doc.id) {
                <tr>
                  <td>{{ doc.title }}</td>
                  <td>{{ doc.documentTypeName ?? doc.documentTypeId ?? '-' }}</td>
                  <td>
                    <select [ngModel]="doc.status" (ngModelChange)="changeStatus(doc, $event)">
                      @for (status of statuses; track status) {
                        <option [value]="status">{{ status }}</option>
                      }
                    </select>
                  </td>
                  <td>{{ doc.fileName ?? '-' }}</td>
                  <td class="actions">
                    <button class="btn" (click)="download(doc)">Descargar</button>
                    <button class="btn btn-danger" (click)="remove(doc)">Eliminar</button>
                  </td>
                </tr>
              } @empty {
                <tr><td colspan="5" class="empty">No hay documentos.</td></tr>
              }
            </tbody>
          </table>
        </div>
      </section>
    </div>
  `,
})
export class DocumentsComponent implements OnInit {
  private api = inject(DocumentService);
  private typesApi = inject(DocumentTypeService);
  private auth = inject(AuthService);

  readonly documents = signal<Document[]>([]);
  readonly types = signal<DocumentType[]>([]);
  readonly message = signal<string | null>(null);
  readonly messageType = signal<'info' | 'success' | 'error'>('info');
  readonly statuses = Object.values(DocumentStatus);

  form: Document = { title: '', description: '', documentTypeId: undefined };
  file: File | null = null;

  ngOnInit(): void {
    this.load();
    const orgId = this.auth.currentUser()?.organizationId;
    if (orgId) this.typesApi.listByOrganization(orgId).subscribe({ next: (types) => this.types.set(types), error: () => this.types.set([]) });
  }

  load(): void {
    this.api.list().subscribe({ next: (docs) => this.documents.set(docs), error: (err) => this.show(err.message, 'error') });
  }

  pickFile(event: Event): void {
    this.file = (event.target as HTMLInputElement).files?.[0] ?? null;
  }

  save(): void {
    if (!this.form.title.trim()) return this.show('El titulo es obligatorio.', 'error');
    this.api.create(this.form, this.file).subscribe({
      next: () => {
        this.form = { title: '', description: '', documentTypeId: undefined };
        this.file = null;
        this.show('Documento guardado.', 'success');
        this.load();
      },
      error: (err) => this.show(err.message, 'error'),
    });
  }

  changeStatus(doc: Document, status: DocumentStatus): void {
    if (!doc.id) return;
    this.api.changeStatus(doc.id, status).subscribe({ next: () => this.load(), error: (err) => this.show(err.message, 'error') });
  }

  download(doc: Document): void {
    if (!doc.id) return;
    this.api.download(doc.id).subscribe({
      next: (blob) => {
        const url = URL.createObjectURL(blob);
        const link = document.createElement('a');
        link.href = url;
        link.download = doc.fileName ?? `documento-${doc.id}`;
        link.click();
        URL.revokeObjectURL(url);
      },
      error: (err) => this.show(err.message, 'error'),
    });
  }

  remove(doc: Document): void {
    if (!doc.id || !confirm('Eliminar documento?')) return;
    this.api.delete(doc.id).subscribe({ next: () => this.load(), error: (err) => this.show(err.message, 'error') });
  }

  private show(text: string, type: 'info' | 'success' | 'error'): void {
    this.message.set(text);
    this.messageType.set(type);
  }
}
