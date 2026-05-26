import { UserRole } from './role.model';

export interface User {
  id?: number;
  name: string;
  email: string;
  password?: string;
  role: UserRole;
  isActive?: boolean;
  organizationId?: number;
  organizationName?: string;
  createdAt?: string;
  updatedAt?: string;
}
