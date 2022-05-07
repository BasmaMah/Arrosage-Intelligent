import { IUser } from 'app/entities/user/user.model';
import { IFerme } from 'app/entities/ferme/ferme.model';

export interface IExtraUser {
  id?: number;
  phone?: string | null;
  address?: string | null;
  user?: IUser | null;
  fermes?: IFerme[] | null;
}

export class ExtraUser implements IExtraUser {
  constructor(
    public id?: number,
    public phone?: string | null,
    public address?: string | null,
    public user?: IUser | null,
    public fermes?: IFerme[] | null
  ) {}
}

export function getExtraUserIdentifier(extraUser: IExtraUser): number | undefined {
  return extraUser.id;
}
