import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { FermeComponent } from '../list/ferme.component';
import { FermeDetailComponent } from '../detail/ferme-detail.component';
import { FermeUpdateComponent } from '../update/ferme-update.component';
import { FermeRoutingResolveService } from './ferme-routing-resolve.service';

const fermeRoute: Routes = [
  {
    path: '',
    component: FermeComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: FermeDetailComponent,
    resolve: {
      ferme: FermeRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: FermeUpdateComponent,
    resolve: {
      ferme: FermeRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: FermeUpdateComponent,
    resolve: {
      ferme: FermeRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(fermeRoute)],
  exports: [RouterModule],
})
export class FermeRoutingModule {}
