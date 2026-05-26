export enum UserRole {
  ADMIN = 'ADMIN',
  USER = 'USER',
}

export const ROLE_LABELS: Record<UserRole, string> = {
  [UserRole.ADMIN]: 'Administrador',
  [UserRole.USER]: 'Usuario',
};
