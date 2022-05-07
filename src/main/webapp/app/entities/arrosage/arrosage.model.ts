import dayjs from 'dayjs/esm';
import { IParcelle } from 'app/entities/parcelle/parcelle.model';

export interface IArrosage {
  id?: number;
  date?: dayjs.Dayjs;
  litresEau?: number | null;
  parcelle?: IParcelle | null;
}

export class Arrosage implements IArrosage {
  constructor(public id?: number, public date?: dayjs.Dayjs, public litresEau?: number | null, public parcelle?: IParcelle | null) {}
}

export function getArrosageIdentifier(arrosage: IArrosage): number | undefined {
  return arrosage.id;
}
