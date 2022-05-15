import dayjs from 'dayjs/esm';
import { IParcelle } from 'app/entities/parcelle/parcelle.model';

export interface INotification {
  id?: number;
  date?: dayjs.Dayjs;
  content?: string;
  vu?: boolean;
  parcelle?: IParcelle | null;
}

export class Notification implements INotification {
  constructor(
    public id?: number,
    public date?: dayjs.Dayjs,
    public content?: string,
    public vu?: boolean,
    public parcelle?: IParcelle | null
  ) {
    this.vu = this.vu ?? false;
  }
}

export function getNotificationIdentifier(notification: INotification): number | undefined {
  return notification.id;
}
