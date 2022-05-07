import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IFerme } from '../ferme.model';
import { FermeService } from '../service/ferme.service';
import { FermeDeleteDialogComponent } from '../delete/ferme-delete-dialog.component';
import { DataUtils } from 'app/core/util/data-util.service';

@Component({
  selector: 'jhi-ferme',
  templateUrl: './ferme.component.html',
})
export class FermeComponent implements OnInit {
  fermes?: IFerme[];
  isLoading = false;

  constructor(protected fermeService: FermeService, protected dataUtils: DataUtils, protected modalService: NgbModal) {}

  loadAll(): void {
    this.isLoading = true;

    this.fermeService.query().subscribe({
      next: (res: HttpResponse<IFerme[]>) => {
        this.isLoading = false;
        this.fermes = res.body ?? [];
      },
      error: () => {
        this.isLoading = false;
      },
    });
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(index: number, item: IFerme): number {
    return item.id!;
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    return this.dataUtils.openFile(base64String, contentType);
  }

  delete(ferme: IFerme): void {
    const modalRef = this.modalService.open(FermeDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.ferme = ferme;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
