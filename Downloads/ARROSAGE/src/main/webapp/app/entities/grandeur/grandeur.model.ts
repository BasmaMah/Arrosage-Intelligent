import dayjs from 'dayjs/esm';
import { IParcelle } from 'app/entities/parcelle/parcelle.model';

export interface IGrandeur {
  id?: number;
  type?: string;
  valeur?: number;
  unite?: string;
  date?: dayjs.Dayjs;
  parcelle?: IParcelle | null;
}

export class Grandeur implements IGrandeur {
  constructor(
    public id?: number,
    public type?: string,
    public valeur?: number,
    public unite?: string,
    public date?: dayjs.Dayjs,
    public parcelle?: IParcelle | null
  ) {}
}

export function getGrandeurIdentifier(grandeur: IGrandeur): number | undefined {
  return grandeur.id;
}
