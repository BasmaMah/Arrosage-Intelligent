import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IPlantage, Plantage } from '../plantage.model';
import { PlantageService } from '../service/plantage.service';

@Injectable({ providedIn: 'root' })
export class PlantageRoutingResolveService implements Resolve<IPlantage> {
  constructor(protected service: PlantageService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IPlantage> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((plantage: HttpResponse<Plantage>) => {
          if (plantage.body) {
            return of(plantage.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Plantage());
  }
}
