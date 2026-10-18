import { Component } from '@angular/core';

import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { finalize } from 'rxjs';
import { AuthService } from '../core/auth.service';
import { ApiError } from '../core/models';
@Component({ selector: 'app-login', standalone: true, imports: [ReactiveFormsModule], template: `
<main class="login-page"><section class="login-story"><a class="brand" href="/"><span class="brand-mark">U</span><span>UGB <small>UNIVERSITÉ GASTON BERGER</small></span></a><div class="story-copy"><span class="eyebrow">ESPACE PROJETS TUTORÉS</span><h1>Les idées prennent forme ici.</h1><p>Retrouvez les sujets, suivez les candidatures et accompagnez chaque projet jusqu’à son affectation.</p><div class="story-note"><span class="note-icon">✦</span><span><strong>Une plateforme, quatre espaces</strong><small>Étudiant · Enseignant · Responsable · Administration</small></span></div></div><span class="story-footer">Saint-Louis · Sénégal</span></section><section class="login-form-wrap"><form class="login-card" [formGroup]="form" (ngSubmit)="submit()"><div class="mobile-brand">UGB <span>· Projets tutorés</span></div><span class="eyebrow">BIENVENUE</span><h2>Connectez-vous</h2><p class="muted">Accédez à votre espace de gestion.</p><label for="email">Adresse e-mail</label><input id="email" type="email" formControlName="email" placeholder="nom@ugb.edu.sn" autocomplete="username">@if (form.controls.email.touched && form.controls.email.invalid) {
<small class="field-error">Saisissez une adresse e-mail valide.</small>
}<label for="password">Mot de passe</label><input id="password" type="password" formControlName="motDePasse" placeholder="Votre mot de passe" autocomplete="current-password">@if (form.controls.motDePasse.touched && form.controls.motDePasse.invalid) {
<small class="field-error">Le mot de passe est obligatoire.</small>
}@if (error) {
<div class="alert error">{{ error }}</div>
}<button class="button primary full" [disabled]="form.invalid || loading">{{ loading ? 'Connexion…' : 'Accéder à mon espace' }} <span>→</span></button><p class="login-hint">Utilisez les identifiants fournis par votre établissement.</p></form></section></main>` })
export class LoginComponent {
  form = this.fb.nonNullable.group({ email: ['', [Validators.required, Validators.email]], motDePasse: ['', Validators.required] });
  error = ''; loading = false;
  constructor(private fb: FormBuilder, private auth: AuthService, private router: Router, private route: ActivatedRoute) {}
  submit(): void { if (this.form.invalid || this.loading) return; this.loading = true; this.error = ''; const { email, motDePasse } = this.form.getRawValue(); this.auth.login(email, motDePasse).pipe(finalize(() => this.loading = false)).subscribe({ next: () => void this.router.navigateByUrl(this.route.snapshot.queryParamMap.get('retour') || '/espace'), error: (e: { error?: ApiError }) => this.error = e.error?.message || 'Connexion impossible. Vérifiez vos identifiants.' }); }
}
