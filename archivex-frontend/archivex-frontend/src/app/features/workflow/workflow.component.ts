import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { WorkflowService } from '../../core/services/workflow.service';
import { DocumentWorkflow, WorkflowStep, WorkflowTaskStatus } from '../../core/models/document.model';
import { StatusMessageComponent } from '../../shared/components/atoms/status-message/status-message.component';

@Component({
  selector: 'app-workflow',
  standalone: true,
  imports: [FormsModule, StatusMessageComponent],
  template: `
    <div class="page">
      <header class="page-header"><div><h1>Workflow</h1><p>Pasos, tareas pendientes y resolucion.</p></div></header>
      <app-status-message [message]="message()" [type]="messageType()" />

      <section class="card form-grid">
        <h2 class="span-3">Pasos por tipo documental</h2>
        <label>Tipo documental ID<input type="number" [(ngModel)]="documentTypeId" /></label>
        <label>Nombre paso<input [(ngModel)]="stepForm.name" /></label>
        <label>Orden<input type="number" [(ngModel)]="stepForm.stepOrder" /></label>
        <label class="span-3">Descripcion<textarea [(ngModel)]="stepForm.description"></textarea></label>
        <button class="btn" (click)="loadSteps()">Consultar pasos</button>
        <button class="btn btn-primary" (click)="createStep()">Crear paso</button>
      </section>

      <section class="card form-grid">
        <h2 class="span-3">Asignar tarea</h2>
        <label>Documento ID<input type="number" [(ngModel)]="taskForm.documentId" /></label>
        <label>Paso ID<input type="number" [(ngModel)]="taskForm.workflowStepId" /></label>
        <label>Usuario asignado ID<input type="number" [(ngModel)]="taskForm.assignedToId" /></label>
        <button class="btn btn-primary" (click)="assignTask()">Asignar tarea</button>
        <button class="btn" (click)="loadPending()">Mis pendientes</button>
      </section>

      <section class="card">
        <h2>Pasos</h2>
        <div class="table-wrap"><table><thead><tr><th>ID</th><th>Nombre</th><th>Orden</th><th>Acciones</th></tr></thead><tbody>
          @for (step of steps(); track step.id) {
            <tr><td>{{ step.id }}</td><td>{{ step.name }}</td><td>{{ step.stepOrder }}</td><td><button class="btn btn-danger" (click)="deleteStep(step)">Eliminar</button></td></tr>
          } @empty { <tr><td colspan="4" class="empty">Sin pasos cargados.</td></tr> }
        </tbody></table></div>
      </section>

      <section class="card">
        <h2>Tareas</h2>
        <div class="table-wrap"><table><thead><tr><th>ID</th><th>Documento</th><th>Paso</th><th>Asignado</th><th>Estado</th><th>Resolver</th></tr></thead><tbody>
          @for (task of tasks(); track task.id) {
            <tr><td>{{ task.id }}</td><td>{{ task.documentTitle ?? task.documentId }}</td><td>{{ task.workflowStepName ?? task.workflowStepId }}</td><td>{{ task.assignedToName ?? task.assignedToId }}</td><td>{{ task.status }}</td><td class="actions"><button class="btn" (click)="resolve(task, completedStatus)">Completar</button><button class="btn btn-danger" (click)="resolve(task, rejectedStatus)">Rechazar</button></td></tr>
          } @empty { <tr><td colspan="6" class="empty">Sin tareas.</td></tr> }
        </tbody></table></div>
      </section>
    </div>
  `,
})
export class WorkflowComponent {
  private api = inject(WorkflowService);
  readonly steps = signal<WorkflowStep[]>([]);
  readonly tasks = signal<DocumentWorkflow[]>([]);
  readonly message = signal<string | null>(null);
  readonly messageType = signal<'info' | 'success' | 'error'>('info');
  readonly completedStatus = WorkflowTaskStatus.COMPLETED;
  readonly rejectedStatus = WorkflowTaskStatus.REJECTED;
  documentTypeId = 0;
  stepForm: WorkflowStep = { name: '', description: '', stepOrder: 1, isActive: true, documentTypeId: 0 };
  taskForm: DocumentWorkflow = { documentId: 0, workflowStepId: 0, assignedToId: 0, status: WorkflowTaskStatus.PENDING };

  loadSteps(): void { if (this.documentTypeId) this.api.listSteps(this.documentTypeId).subscribe({ next: (steps) => this.steps.set(steps), error: (err) => this.show(err.message, 'error') }); }
  createStep(): void { this.api.createStep({ ...this.stepForm, documentTypeId: this.documentTypeId || this.stepForm.documentTypeId }).subscribe({ next: () => { this.show('Paso creado.', 'success'); this.loadSteps(); }, error: (err) => this.show(err.message, 'error') }); }
  deleteStep(step: WorkflowStep): void { if (!step.id) return; this.api.deleteStep(step.id).subscribe({ next: () => this.loadSteps(), error: (err) => this.show(err.message, 'error') }); }
  assignTask(): void { this.api.assignTask(this.taskForm).subscribe({ next: () => { this.show('Tarea asignada.', 'success'); this.loadPending(); }, error: (err) => this.show(err.message, 'error') }); }
  loadPending(): void { this.api.listPendingTasks().subscribe({ next: (tasks) => this.tasks.set(tasks), error: (err) => this.show(err.message, 'error') }); }
  resolve(task: DocumentWorkflow, status: WorkflowTaskStatus): void { if (!task.id) return; this.api.resolveTask(task.id, status, 'Resuelto desde frontend').subscribe({ next: () => this.loadPending(), error: (err) => this.show(err.message, 'error') }); }
  private show(text: string, type: 'info' | 'success' | 'error'): void { this.message.set(text); this.messageType.set(type); }
}
