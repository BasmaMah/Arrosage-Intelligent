import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';

import { IArrosage, Arrosage } from '../arrosage.model';
import { ArrosageService } from '../service/arrosage.service';
import { IParcelle } from 'app/entities/parcelle/parcelle.model';
import { ParcelleService } from 'app/entities/parcelle/service/parcelle.service';

@Component({
  selector: 'jhi-arrosage-update',
  templateUrl: './arrosage-update.component.html',
})
export class ArrosageUpdateComponent implements OnInit {
  isSaving = false;

  parcellesSharedCollection: IParcelle[] = [];

  editForm = this.fb.group({
    id: [],
    date: [null, [Validators.required]],
    litresEau: [],
    parcelle: [],
  });

  constructor(
    protected arrosageService: ArrosageService,
    protected parcelleService: ParcelleService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ arrosage }) => {
      if (arrosage.id === undefined) {
        const today = dayjs().startOf('day');
        arrosage.date = today;
      }

      this.updateForm(arrosage);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const arrosage = this.createFromForm();
    if (arrosage.id !== undefined) {
      this.subscribeToSaveResponse(this.arrosageService.update(arrosage));
    } else {
      this.subscribeToSaveResponse(this.arrosageService.create(arrosage));
    }
  }

  trackParcelleById(index: number, item: IParcelle): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IArrosage>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(arrosage: IArrosage): void {
    this.editForm.patchValue({
      id: arrosage.id,
      date: arrosage.date ? arrosage.date.format(DATE_TIME_FORMAT) : null,
      litresEau: arrosage.litresEau,
      parcelle: arrosage.parcelle,
    });

    this.parcellesSharedCollection = this.parcelleService.addParcelleToCollectionIfMissing(
      this.parcellesSharedCollection,
      arrosage.parcelle
    );
  }

  protected loadRelationshipsOptions(): void {
    this.parcelleService
      .query()
      .pipe(map((res: HttpResponse<IParcelle[]>) => res.body ?? []))
      .pipe(
        map((parcelles: IParcelle[]) =>
          this.parcelleService.addParcelleToCollectionIfMissing(parcelles, this.editForm.get('parcelle')!.value)
        )
      )
      .subscribe((parcelles: IParcelle[]) => (this.parcellesSharedCollection = parcelles));
  }

  protected createFromForm(): IArrosage {
    return {
      ...new Arrosage(),
      id: this.editForm.get(['id'])!.value,
      date: this.editForm.get(['date'])!.value ? dayjs(this.editForm.get(['date'])!.value, DATE_TIME_FORMAT) : undefined,
      litresEau: this.editForm.get(['litresEau'])!.value,
      parcelle: this.editForm.get(['parcelle'])!.value,
    };
  }
}
