import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IFerme, getFermeIdentifier } from '../ferme.model';

export type EntityResponseType = HttpResponse<IFerme>;
export type EntityArrayResponseType = HttpResponse<IFerme[]>;

@Injectable({ providedIn: 'root' })
export class FermeService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/fermes');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(ferme: IFerme): Observable<EntityResponseType> {
    return this.http.post<IFerme>(this.resourceUrl, ferme, { observe: 'response' });
  }

  update(ferme: IFerme): Observable<EntityResponseType> {
    return this.http.put<IFerme>(`${this.resourceUrl}/${getFermeIdentifier(ferme) as number}`, ferme, { observe: 'response' });
  }

  partialUpdate(ferme: IFerme): Observable<EntityResponseType> {
    return this.http.patch<IFerme>(`${this.resourceUrl}/${getFermeIdentifier(ferme) as number}`, ferme, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IFerme>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IFerme[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addFermeToCollectionIfMissing(fermeCollection: IFerme[], ...fermesToCheck: (IFerme | null | undefined)[]): IFerme[] {
    const fermes: IFerme[] = fermesToCheck.filter(isPresent);
    if (fermes.length > 0) {
      const fermeCollectionIdentifiers = fermeCollection.map(fermeItem => getFermeIdentifier(fermeItem)!);
      const fermesToAdd = fermes.filter(fermeItem => {
        const fermeIdentifier = getFermeIdentifier(fermeItem);
        if (fermeIdentifier == null || fermeCollectionIdentifiers.includes(fermeIdentifier)) {
          return false;
        }
        fermeCollectionIdentifiers.push(fermeIdentifier);
        return true;
      });
      return [...fermesToAdd, ...fermeCollection];
    }
    return fermeCollection;
  }
}
