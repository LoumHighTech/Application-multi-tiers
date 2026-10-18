import { Routes } from '@angular/router';
import { LoginComponent } from './auth/login.component';
import { PortalComponent } from './portal/portal.component';
import { authGuard } from './core/guards';
import { Role } from './core/models';
const roles = (...allowed: Role[]) => ({ roles: allowed });
export const routes: Routes = [
  { path: 'connexion', component: LoginComponent },
  { path: 'espace', component: PortalComponent, canActivate: [authGuard] },
  { path: 'admin', component: PortalComponent, canActivate: [authGuard], data: roles('ADMIN') },
  { path: 'enseignant', component: PortalComponent, canActivate: [authGuard], data: roles('ENSEIGNANT') },
  { path: 'etudiant', component: PortalComponent, canActivate: [authGuard], data: roles('ETUDIANT') },
  { path: 'responsable', component: PortalComponent, canActivate: [authGuard], data: roles('RESPONSABLE') },
  { path: '', pathMatch: 'full', redirectTo: 'espace' },
  { path: '**', redirectTo: 'espace' }
];
