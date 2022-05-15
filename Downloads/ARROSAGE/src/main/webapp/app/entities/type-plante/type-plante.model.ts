import { IPlante } from 'app/entities/plante/plante.model';

export interface ITypePlante {
  id?: number;
  libelle?: string;
  humiditeMax?: number | null;
  humiditeMin?: number | null;
  temperature?: number | null;
  luminosite?: number | null;
  plantes?: IPlante[] | null;
}

export class TypePlante implements ITypePlante {
  constructor(
    public id?: number,
    public libelle?: string,
    public humiditeMax?: number | null,
    public humiditeMin?: number | null,
    public temperature?: number | null,
    public luminosite?: number | null,
    public plantes?: IPlante[] | null
  ) {}
}

export function getTypePlanteIdentifier(typePlante: ITypePlante): number | undefined {
  return typePlante.id;
}
