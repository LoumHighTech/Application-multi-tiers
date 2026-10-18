import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Candidature, Enseignant, Etudiant, ProjetAdmin, Statut, Sujet } from './models';
const api = environment.apiUrl;

@Injectable({ providedIn: 'root' }) export class SujetsService {
  constructor(private http: HttpClient) {}
  disponibles(): Observable<Sujet[]> { return this.http.get<Sujet[]>(`${api}/sujets/disponibles`); }
  tous(): Observable<Sujet[]> { return this.http.get<Sujet[]>(`${api}/sujets`); }
  miens(): Observable<Sujet[]> { return this.http.get<Sujet[]>(`${api}/sujets/mes-sujets`); }
  creer(data: { titre: string; description: string; capaciteMax: number }): Observable<Sujet> { return this.http.post<Sujet>(`${api}/sujets`, data); }
  modifier(id: number, data: { titre: string; description: string; capaciteMax: number }): Observable<Sujet> { return this.http.put<Sujet>(`${api}/sujets/${id}`, data); }
  supprimer(id: number): Observable<void> { return this.http.delete<void>(`${api}/sujets/${id}`); }
}
@Injectable({ providedIn: 'root' }) export class CandidaturesService {
  constructor(private http: HttpClient) {}
  mesCandidatures(): Observable<Candidature[]> { return this.http.get<Candidature[]>(`${api}/candidatures/mes-candidatures`); }
  toutes(statut?: Statut | ''): Observable<Candidature[]> { let params = new HttpParams(); if (statut) params = params.set('statut', statut); return this.http.get<Candidature[]>(`${api}/candidatures`, { params }); }
  parSujet(id: number): Observable<Candidature[]> { return this.http.get<Candidature[]>(`${api}/candidatures/sujet/${id}`); }
  deposer(sujetId: number, motivation: string): Observable<Candidature> { return this.http.post<Candidature>(`${api}/candidatures`, { sujetId, motivation }); }
  accepter(id: number): Observable<Candidature> { return this.http.put<Candidature>(`${api}/candidatures/${id}/accepter`, {}); }
  rejeter(id: number): Observable<Candidature> { return this.http.put<Candidature>(`${api}/candidatures/${id}/rejeter`, {}); }
}
@Injectable({ providedIn: 'root' }) export class EtudiantsService {
  constructor(private http: HttpClient) {}
  tous(): Observable<Etudiant[]> { return this.http.get<Etudiant[]>(`${api}/etudiants`); }
  creer(data: object): Observable<Etudiant> { return this.http.post<Etudiant>(`${api}/etudiants`, data); }
  modifier(id: number, data: object): Observable<Etudiant> { return this.http.put<Etudiant>(`${api}/etudiants/${id}`, data); }
  supprimer(id: number): Observable<void> { return this.http.delete<void>(`${api}/etudiants/${id}`); }
}
@Injectable({ providedIn: 'root' }) export class EnseignantsService {
  constructor(private http: HttpClient) {}
  tous(): Observable<Enseignant[]> { return this.http.get<Enseignant[]>(`${api}/enseignants`); }
  creer(data: object): Observable<Enseignant> { return this.http.post<Enseignant>(`${api}/enseignants`, data); }
  modifier(id: number, data: object): Observable<Enseignant> { return this.http.put<Enseignant>(`${api}/enseignants/${id}`, data); }
  supprimer(id: number): Observable<void> { return this.http.delete<void>(`${api}/enseignants/${id}`); }
}
@Injectable({ providedIn: 'root' }) export class AdminProjetsService {
  constructor(private http: HttpClient) {}
  tous(): Observable<ProjetAdmin[]> { return this.http.get<ProjetAdmin[]>(`${api}/admin/projets`); }
}
@Injectable({ providedIn: 'root' }) export class AffectationsService {
  constructor(private http: HttpClient) {}
  toutes(): Observable<Candidature[]> { return this.http.get<Candidature[]>(`${api}/affectations`); }
}
