import { IConnecte } from 'app/entities/connecte/connecte.model';
import { IBoitier } from 'app/entities/boitier/boitier.model';

export interface ICapteur {
  id?: number;
  ref?: string | null;
  type?: string | null;
  photoContentType?: string | null;
  photo?: string | null;
  connectes?: IConnecte[] | null;
  boitier?: IBoitier | null;
}

export class Capteur implements ICapteur {
  constructor(
    public id?: number,
    public ref?: string | null,
    public type?: string | null,
    public photoContentType?: string | null,
    public photo?: string | null,
    public connectes?: IConnecte[] | null,
    public boitier?: IBoitier | null
  ) {}
}

export function getCapteurIdentifier(capteur: ICapteur): number | undefined {
  return capteur.id;
}
