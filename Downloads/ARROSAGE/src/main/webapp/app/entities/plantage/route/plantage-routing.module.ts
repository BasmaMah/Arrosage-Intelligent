import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { PlantageComponent } from '../list/plantage.component';
import { PlantageDetailComponent } from '../detail/plantage-detail.component';
import { PlantageUpdateComponent } from '../update/plantage-update.component';
import { PlantageRoutingResolveService } from './plantage-routing-resolve.service';

const plantageRoute: Routes = [
  {
    path: '',
    component: PlantageComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: PlantageDetailComponent,
    resolve: {
      plantage: PlantageRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: PlantageUpdateComponent,
    resolve: {
      plantage: PlantageRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: PlantageUpdateComponent,
    resolve: {
      plantage: PlantageRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(plantageRoute)],
  exports: [RouterModule],
})
export class PlantageRoutingModule {}
