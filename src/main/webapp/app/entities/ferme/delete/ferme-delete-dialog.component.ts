import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IFerme } from '../ferme.model';
import { FermeService } from '../service/ferme.service';

@Component({
  templateUrl: './ferme-delete-dialog.component.html',
})
export class FermeDeleteDialogComponent {
  ferme?: IFerme;

  constructor(protected fermeService: FermeService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.fermeService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
