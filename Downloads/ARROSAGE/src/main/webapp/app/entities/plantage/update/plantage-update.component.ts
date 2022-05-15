import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';

import { IPlantage, Plantage } from '../plantage.model';
import { PlantageService } from '../service/plantage.service';
import { IParcelle } from 'app/entities/parcelle/parcelle.model';
import { ParcelleService } from 'app/entities/parcelle/service/parcelle.service';
import { IPlante } from 'app/entities/plante/plante.model';
import { PlanteService } from 'app/entities/plante/service/plante.service';

@Component({
  selector: 'jhi-plantage-update',
  templateUrl: './plantage-update.component.html',
})
export class PlantageUpdateComponent implements OnInit {
  isSaving = false;

  parcellesSharedCollection: IParcelle[] = [];
  plantesSharedCollection: IPlante[] = [];

  editForm = this.fb.group({
    id: [],
    date: [null, [Validators.required]],
    nombre: [null, [Validators.required]],
    parcelle: [],
    plante: [],
  });

  constructor(
    protected plantageService: PlantageService,
    protected parcelleService: ParcelleService,
    protected planteService: PlanteService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ plantage }) => {
      if (plantage.id === undefined) {
        const today = dayjs().startOf('day');
        plantage.date = today;
      }

      this.updateForm(plantage);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const plantage = this.createFromForm();
    if (plantage.id !== undefined) {
      this.subscribeToSaveResponse(this.plantageService.update(plantage));
    } else {
      this.subscribeToSaveResponse(this.plantageService.create(plantage));
    }
  }

  trackParcelleById(index: number, item: IParcelle): number {
    return item.id!;
  }

  trackPlanteById(index: number, item: IPlante): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IPlantage>>): void {
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

  protected updateForm(plantage: IPlantage): void {
    this.editForm.patchValue({
      id: plantage.id,
      date: plantage.date ? plantage.date.format(DATE_TIME_FORMAT) : null,
      nombre: plantage.nombre,
      parcelle: plantage.parcelle,
      plante: plantage.plante,
    });

    this.parcellesSharedCollection = this.parcelleService.addParcelleToCollectionIfMissing(
      this.parcellesSharedCollection,
      plantage.parcelle
    );
    this.plantesSharedCollection = this.planteService.addPlanteToCollectionIfMissing(this.plantesSharedCollection, plantage.plante);
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

    this.planteService
      .query()
      .pipe(map((res: HttpResponse<IPlante[]>) => res.body ?? []))
      .pipe(map((plantes: IPlante[]) => this.planteService.addPlanteToCollectionIfMissing(plantes, this.editForm.get('plante')!.value)))
      .subscribe((plantes: IPlante[]) => (this.plantesSharedCollection = plantes));
  }

  protected createFromForm(): IPlantage {
    return {
      ...new Plantage(),
      id: this.editForm.get(['id'])!.value,
      date: this.editForm.get(['date'])!.value ? dayjs(this.editForm.get(['date'])!.value, DATE_TIME_FORMAT) : undefined,
      nombre: this.editForm.get(['nombre'])!.value,
      parcelle: this.editForm.get(['parcelle'])!.value,
      plante: this.editForm.get(['plante'])!.value,
    };
  }
}
