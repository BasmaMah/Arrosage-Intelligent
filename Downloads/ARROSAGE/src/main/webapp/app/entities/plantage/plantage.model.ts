import dayjs from 'dayjs/esm';
import { IParcelle } from 'app/entities/parcelle/parcelle.model';
import { IPlante } from 'app/entities/plante/plante.model';

export interface IPlantage {
  id?: number;
  date?: dayjs.Dayjs;
  nombre?: number;
  parcelle?: IParcelle | null;
  plante?: IPlante | null;
}

export class Plantage implements IPlantage {
  constructor(
    public id?: number,
    public date?: dayjs.Dayjs,
    public nombre?: number,
    public parcelle?: IParcelle | null,
    public plante?: IPlante | null
  ) {}
}

export function getPlantageIdentifier(plantage: IPlantage): number | undefined {
  return plantage.id;
}
