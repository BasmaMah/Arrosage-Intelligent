import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { PlantageComponent } from './list/plantage.component';
import { PlantageDetailComponent } from './detail/plantage-detail.component';
import { PlantageUpdateComponent } from './update/plantage-update.component';
import { PlantageDeleteDialogComponent } from './delete/plantage-delete-dialog.component';
import { PlantageRoutingModule } from './route/plantage-routing.module';

@NgModule({
  imports: [SharedModule, PlantageRoutingModule],
  declarations: [PlantageComponent, PlantageDetailComponent, PlantageUpdateComponent, PlantageDeleteDialogComponent],
  entryComponents: [PlantageDeleteDialogComponent],
})
export class PlantageModule {}
