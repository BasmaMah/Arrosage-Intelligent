import { IInstallation } from 'app/entities/installation/installation.model';
import { IPlantage } from 'app/entities/plantage/plantage.model';
import { IArrosage } from 'app/entities/arrosage/arrosage.model';
import { INotification } from 'app/entities/notification/notification.model';
import { IGrandeur } from 'app/entities/grandeur/grandeur.model';
import { ITypeSol } from 'app/entities/type-sol/type-sol.model';
import { IFerme } from 'app/entities/ferme/ferme.model';

export interface IParcelle {
  id?: number;
  surface?: number | null;
  photoContentType?: string | null;
  photo?: string | null;
  libelle?: string | null;
  installations?: IInstallation[] | null;
  plantages?: IPlantage[] | null;
  arrosages?: IArrosage[] | null;
  notifications?: INotification[] | null;
  grandeurs?: IGrandeur[] | null;
  typeSol?: ITypeSol | null;
  ferme?: IFerme | null;
}

export class Parcelle implements IParcelle {
  constructor(
    public id?: number,
    public surface?: number | null,
    public photoContentType?: string | null,
    public photo?: string | null,
    public libelle?: string | null,
    public installations?: IInstallation[] | null,
    public plantages?: IPlantage[] | null,
    public arrosages?: IArrosage[] | null,
    public notifications?: INotification[] | null,
    public grandeurs?: IGrandeur[] | null,
    public typeSol?: ITypeSol | null,
    public ferme?: IFerme | null
  ) {}
}

export function getParcelleIdentifier(parcelle: IParcelle): number | undefined {
  return parcelle.id;
}
