import dayjs from 'dayjs/esm';
import { IParcelle } from 'app/entities/parcelle/parcelle.model';
import { IBoitier } from 'app/entities/boitier/boitier.model';

export interface IInstallation {
  id?: number;
  dateDebut?: dayjs.Dayjs;
  dateFin?: dayjs.Dayjs | null;
  parcelle?: IParcelle | null;
  boitier?: IBoitier | null;
}

export class Installation implements IInstallation {
  constructor(
    public id?: number,
    public dateDebut?: dayjs.Dayjs,
    public dateFin?: dayjs.Dayjs | null,
    public parcelle?: IParcelle | null,
    public boitier?: IBoitier | null
  ) {}
}

export function getInstallationIdentifier(installation: IInstallation): number | undefined {
  return installation.id;
}
