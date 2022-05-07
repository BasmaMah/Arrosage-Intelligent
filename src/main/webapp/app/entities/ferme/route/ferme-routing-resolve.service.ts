import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IFerme, Ferme } from '../ferme.model';
import { FermeService } from '../service/ferme.service';

@Injectable({ providedIn: 'root' })
export class FermeRoutingResolveService implements Resolve<IFerme> {
  constructor(protected service: FermeService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IFerme> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((ferme: HttpResponse<Ferme>) => {
          if (ferme.body) {
            return of(ferme.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Ferme());
  }
}
