import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from './auth.service';
import { Role } from './models';
export const authGuard: CanActivateFn = (_route, state) => {
  const auth = inject(AuthService); const router = inject(Router);
  if (!auth.isAuthenticated()) return router.createUrlTree(['/connexion'], { queryParams: { retour: state.url } });
  const allowed = _route.data['roles'] as Role[] | undefined;
  return !allowed || auth.hasRole(allowed) ? true : router.createUrlTree(['/espace']);
};
