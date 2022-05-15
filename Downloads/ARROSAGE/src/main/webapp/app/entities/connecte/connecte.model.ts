import { IBoitier } from 'app/entities/boitier/boitier.model';
import { ICapteur } from 'app/entities/capteur/capteur.model';

export interface IConnecte {
  id?: number;
  fonctionnel?: boolean | null;
  branche?: string | null;
  frequence?: number | null;
  marge?: number | null;
  boitier?: IBoitier | null;
  capteur?: ICapteur | null;
}

export class Connecte implements IConnecte {
  constructor(
    public id?: number,
    public fonctionnel?: boolean | null,
    public branche?: string | null,
    public frequence?: number | null,
    public marge?: number | null,
    public boitier?: IBoitier | null,
    public capteur?: ICapteur | null
  ) {
    this.fonctionnel = this.fonctionnel ?? false;
  }
}

export function getConnecteIdentifier(connecte: IConnecte): number | undefined {
  return connecte.id;
}
