import { IPlantage } from 'app/entities/plantage/plantage.model';
import { ITypePlante } from 'app/entities/type-plante/type-plante.model';

export interface IPlante {
  id?: number;
  libelle?: string;
  photoContentType?: string | null;
  photo?: string | null;
  racine?: string | null;
  plantages?: IPlantage[] | null;
  typePlante?: ITypePlante | null;
}

export class Plante implements IPlante {
  constructor(
    public id?: number,
    public libelle?: string,
    public photoContentType?: string | null,
    public photo?: string | null,
    public racine?: string | null,
    public plantages?: IPlantage[] | null,
    public typePlante?: ITypePlante | null
  ) {}
}

export function getPlanteIdentifier(plante: IPlante): number | undefined {
  return plante.id;
}
