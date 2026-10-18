export type Role = 'ADMIN' | 'ENSEIGNANT' | 'ETUDIANT' | 'RESPONSABLE';
export type Statut = 'EN_ATTENTE' | 'ACCEPTEE' | 'REJETEE';
export interface Session { token: string; type: string; id: number; nom: string; prenom: string; email: string; role: Role; }
export interface Sujet { id: number; titre: string; description: string; capaciteMax: number; disponible: boolean; dateCreation: string; enseignantId: number; enseignantNom: string; nombreCandidatures: number; nombreAcceptees: number; placesRestantes: number; }
export interface Candidature { id: number; sujetId: number; sujetTitre: string; enseignantNom: string; etudiantId: number; etudiantNom: string; numeroEtudiant: string; filiere: string; motivation: string; statut: Statut; dateCandidature: string; dateDecision?: string; responsableNom?: string; }
export interface Etudiant { id: number; nom: string; prenom: string; email: string; numeroEtudiant: string; filiere: string; role: Role; }
export interface Enseignant { id: number; nom: string; prenom: string; email: string; departement: string; role: Role; }
export interface ProjetAdmin extends Sujet {}
export interface ApiError { status?: number; error?: string; message?: string; path?: string; details?: { field: string; message: string }[]; }
