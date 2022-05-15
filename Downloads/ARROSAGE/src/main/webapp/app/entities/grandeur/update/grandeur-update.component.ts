import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';

import { IGrandeur, Grandeur } from '../grandeur.model';
import { GrandeurService } from '../service/grandeur.service';
import { IParcelle } from 'app/entities/parcelle/parcelle.model';
import { ParcelleService } from 'app/entities/parcelle/service/parcelle.service';

@Component({
  selector: 'jhi-grandeur-update',
  templateUrl: './grandeur-update.component.html',
})
export class GrandeurUpdateComponent implements OnInit {
  isSaving = false;

  parcellesSharedCollection: IParcelle[] = [];

  editForm = this.fb.group({
    id: [],
    type: [null, [Validators.required]],
    valeur: [null, [Validators.required]],
    unite: [null, [Validators.required]],
    date: [null, [Validators.required]],
    parcelle: [],
  });

  constructor(
    protected grandeurService: GrandeurService,
    protected parcelleService: ParcelleService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ grandeur }) => {
      if (grandeur.id === undefined) {
        const today = dayjs().startOf('day');
        grandeur.date = today;
      }

      this.updateForm(grandeur);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const grandeur = this.createFromForm();
    if (grandeur.id !== undefined) {
      this.subscribeToSaveResponse(this.grandeurService.update(grandeur));
    } else {
      this.subscribeToSaveResponse(this.grandeurService.create(grandeur));
    }
  }

  trackParcelleById(index: number, item: IParcelle): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IGrandeur>>): void {
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

  protected updateForm(grandeur: IGrandeur): void {
    this.editForm.patchValue({
      id: grandeur.id,
      type: grandeur.type,
      valeur: grandeur.valeur,
      unite: grandeur.unite,
      date: grandeur.date ? grandeur.date.format(DATE_TIME_FORMAT) : null,
      parcelle: grandeur.parcelle,
    });

    this.parcellesSharedCollection = this.parcelleService.addParcelleToCollectionIfMissing(
      this.parcellesSharedCollection,
      grandeur.parcelle
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

  protected createFromForm(): IGrandeur {
    return {
      ...new Grandeur(),
      id: this.editForm.get(['id'])!.value,
      type: this.editForm.get(['type'])!.value,
      valeur: this.editForm.get(['valeur'])!.value,
      unite: this.editForm.get(['unite'])!.value,
      date: this.editForm.get(['date'])!.value ? dayjs(this.editForm.get(['date'])!.value, DATE_TIME_FORMAT) : undefined,
      parcelle: this.editForm.get(['parcelle'])!.value,
    };
  }
}
