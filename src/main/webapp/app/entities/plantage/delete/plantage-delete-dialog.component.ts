import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IPlantage } from '../plantage.model';
import { PlantageService } from '../service/plantage.service';

@Component({
  templateUrl: './plantage-delete-dialog.component.html',
})
export class PlantageDeleteDialogComponent {
  plantage?: IPlantage;

  constructor(protected plantageService: PlantageService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.plantageService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
