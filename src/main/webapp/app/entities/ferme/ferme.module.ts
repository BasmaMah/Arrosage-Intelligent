import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { FermeComponent } from './list/ferme.component';
import { FermeDetailComponent } from './detail/ferme-detail.component';
import { FermeUpdateComponent } from './update/ferme-update.component';
import { FermeDeleteDialogComponent } from './delete/ferme-delete-dialog.component';
import { FermeRoutingModule } from './route/ferme-routing.module';

@NgModule({
  imports: [SharedModule, FermeRoutingModule],
  declarations: [FermeComponent, FermeDetailComponent, FermeUpdateComponent, FermeDeleteDialogComponent],
  entryComponents: [FermeDeleteDialogComponent],
})
export class FermeModule {}
