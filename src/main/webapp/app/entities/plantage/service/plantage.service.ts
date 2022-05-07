import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IPlantage, getPlantageIdentifier } from '../plantage.model';

export type EntityResponseType = HttpResponse<IPlantage>;
export type EntityArrayResponseType = HttpResponse<IPlantage[]>;

@Injectable({ providedIn: 'root' })
export class PlantageService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/plantages');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(plantage: IPlantage): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(plantage);
    return this.http
      .post<IPlantage>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(plantage: IPlantage): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(plantage);
    return this.http
      .put<IPlantage>(`${this.resourceUrl}/${getPlantageIdentifier(plantage) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  partialUpdate(plantage: IPlantage): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(plantage);
    return this.http
      .patch<IPlantage>(`${this.resourceUrl}/${getPlantageIdentifier(plantage) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<IPlantage>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IPlantage[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addPlantageToCollectionIfMissing(plantageCollection: IPlantage[], ...plantagesToCheck: (IPlantage | null | undefined)[]): IPlantage[] {
    const plantages: IPlantage[] = plantagesToCheck.filter(isPresent);
    if (plantages.length > 0) {
      const plantageCollectionIdentifiers = plantageCollection.map(plantageItem => getPlantageIdentifier(plantageItem)!);
      const plantagesToAdd = plantages.filter(plantageItem => {
        const plantageIdentifier = getPlantageIdentifier(plantageItem);
        if (plantageIdentifier == null || plantageCollectionIdentifiers.includes(plantageIdentifier)) {
          return false;
        }
        plantageCollectionIdentifiers.push(plantageIdentifier);
        return true;
      });
      return [...plantagesToAdd, ...plantageCollection];
    }
    return plantageCollection;
  }

  protected convertDateFromClient(plantage: IPlantage): IPlantage {
    return Object.assign({}, plantage, {
      date: plantage.date?.isValid() ? plantage.date.toJSON() : undefined,
    });
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.date = res.body.date ? dayjs(res.body.date) : undefined;
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((plantage: IPlantage) => {
        plantage.date = plantage.date ? dayjs(plantage.date) : undefined;
      });
    }
    return res;
  }
}
