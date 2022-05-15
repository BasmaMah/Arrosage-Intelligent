import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { BoitierComponent } from './list/boitier.component';
import { BoitierDetailComponent } from './detail/boitier-detail.component';
import { BoitierUpdateComponent } from './update/boitier-update.component';
import { BoitierDeleteDialogComponent } from './delete/boitier-delete-dialog.component';
import { BoitierRoutingModule } from './route/boitier-routing.module';

@NgModule({
  imports: [SharedModule, BoitierRoutingModule],
  declarations: [BoitierComponent, BoitierDetailComponent, BoitierUpdateComponent, BoitierDeleteDialogComponent],
  entryComponents: [BoitierDeleteDialogComponent],
})
export class BoitierModule {}
