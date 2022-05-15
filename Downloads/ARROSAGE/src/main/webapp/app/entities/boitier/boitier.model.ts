import { ICapteur } from 'app/entities/capteur/capteur.model';
import { IInstallation } from 'app/entities/installation/installation.model';
import { IConnecte } from 'app/entities/connecte/connecte.model';

export interface IBoitier {
  id?: number;
  ref?: number;
  type?: string | null;
  nbrBranchBoitier?: number | null;
  nbrBranchArduino?: number | null;
  code?: string | null;
  capteurs?: ICapteur[] | null;
  installations?: IInstallation[] | null;
  connectes?: IConnecte[] | null;
}

export class Boitier implements IBoitier {
  constructor(
    public id?: number,
    public ref?: number,
    public type?: string | null,
    public nbrBranchBoitier?: number | null,
    public nbrBranchArduino?: number | null,
    public code?: string | null,
    public capteurs?: ICapteur[] | null,
    public installations?: IInstallation[] | null,
    public connectes?: IConnecte[] | null
  ) {}
}

export function getBoitierIdentifier(boitier: IBoitier): number | undefined {
  return boitier.id;
}
