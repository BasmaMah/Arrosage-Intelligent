import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IPlantage } from '../plantage.model';
import { PlantageService } from '../service/plantage.service';
import { PlantageDeleteDialogComponent } from '../delete/plantage-delete-dialog.component';

@Component({
  selector: 'jhi-plantage',
  templateUrl: './plantage.component.html',
})
export class PlantageComponent implements OnInit {
  plantages?: IPlantage[];
  isLoading = false;

  constructor(protected plantageService: PlantageService, protected modalService: NgbModal) {}

  loadAll(): void {
    this.isLoading = true;

    this.plantageService.query().subscribe({
      next: (res: HttpResponse<IPlantage[]>) => {
        this.isLoading = false;
        this.plantages = res.body ?? [];
      },
      error: () => {
        this.isLoading = false;
      },
    });
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(index: number, item: IPlantage): number {
    return item.id!;
  }

  delete(plantage: IPlantage): void {
    const modalRef = this.modalService.open(PlantageDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.plantage = plantage;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
