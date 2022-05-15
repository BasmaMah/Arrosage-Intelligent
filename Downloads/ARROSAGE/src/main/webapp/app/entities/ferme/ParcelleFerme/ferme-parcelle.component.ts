import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IFerme } from '../ferme.model';
import { DataUtils } from 'app/core/util/data-util.service';

@Component({
  selector: 'jhi-ferme-parcelle',
  templateUrl: './ferme-parcelle.component.html',
})
export class FermeParcelleComponent implements OnInit {
  ferme: IFerme | null = null;

  constructor(protected dataUtils: DataUtils, protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ ferme }) => {
      this.ferme = ferme;
    });
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    this.dataUtils.openFile(base64String, contentType);
  }

  previousState(): void {
    window.history.back();
  }
}
