import { IParcelle } from 'app/entities/parcelle/parcelle.model';

export interface ITypeSol {
  id?: number;
  libelle?: string;
  description?: string | null;
  parcelles?: IParcelle[] | null;
}

export class TypeSol implements ITypeSol {
  constructor(public id?: number, public libelle?: string, public description?: string | null, public parcelles?: IParcelle[] | null) {}
}

export function getTypeSolIdentifier(typeSol: ITypeSol): number | undefined {
  return typeSol.id;
}
