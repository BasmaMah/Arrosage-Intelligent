import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IParcelle } from '../parcelle.model';
import { ParcelleService } from '../service/parcelle.service';
import { ParcelleDeleteDialogComponent } from '../delete/parcelle-delete-dialog.component';
import { DataUtils } from 'app/core/util/data-util.service';

@Component({
  selector: 'jhi-parcelle',
  templateUrl: './parcelle.component.html',
})
export class ParcelleComponent implements OnInit {
  parcelles?: IParcelle[];
  isLoading = false;

  constructor(protected parcelleService: ParcelleService, protected dataUtils: DataUtils, protected modalService: NgbModal) {}

  loadAll(): void {
    this.isLoading = true;

    this.parcelleService.query().subscribe({
      next: (res: HttpResponse<IParcelle[]>) => {
        this.isLoading = false;
        this.parcelles = res.body ?? [];
      },
      error: () => {
        this.isLoading = false;
      },
    });
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(index: number, item: IParcelle): number {
    return item.id!;
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    return this.dataUtils.openFile(base64String, contentType);
  }

  delete(parcelle: IParcelle): void {
    const modalRef = this.modalService.open(ParcelleDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.parcelle = parcelle;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
