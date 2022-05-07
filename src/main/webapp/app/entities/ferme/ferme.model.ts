import { IParcelle } from 'app/entities/parcelle/parcelle.model';
import { IExtraUser } from 'app/entities/extra-user/extra-user.model';

export interface IFerme {
  id?: number;
  lebelle?: string | null;
  photoContentType?: string | null;
  photo?: string | null;
  parcelles?: IParcelle[] | null;
  extraUser?: IExtraUser | null;
}

export class Ferme implements IFerme {
  constructor(
    public id?: number,
    public lebelle?: string | null,
    public photoContentType?: string | null,
    public photo?: string | null,
    public parcelles?: IParcelle[] | null,
    public extraUser?: IExtraUser | null
  ) {}
}

export function getFermeIdentifier(ferme: IFerme): number | undefined {
  return ferme.id;
}
