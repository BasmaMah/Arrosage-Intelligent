import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';

import { IInstallation, Installation } from '../installation.model';
import { InstallationService } from '../service/installation.service';
import { IParcelle } from 'app/entities/parcelle/parcelle.model';
import { ParcelleService } from 'app/entities/parcelle/service/parcelle.service';
import { IBoitier } from 'app/entities/boitier/boitier.model';
import { BoitierService } from 'app/entities/boitier/service/boitier.service';

@Component({
  selector: 'jhi-installation-update',
  templateUrl: './installation-update.component.html',
})
export class InstallationUpdateComponent implements OnInit {
  isSaving = false;

  parcellesSharedCollection: IParcelle[] = [];
  boitiersSharedCollection: IBoitier[] = [];

  editForm = this.fb.group({
    id: [],
    dateDebut: [null, [Validators.required]],
    dateFin: [],
    parcelle: [],
    boitier: [],
  });

  constructor(
    protected installationService: InstallationService,
    protected parcelleService: ParcelleService,
    protected boitierService: BoitierService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ installation }) => {
      if (installation.id === undefined) {
        const today = dayjs().startOf('day');
        installation.dateDebut = today;
        installation.dateFin = today;
      }

      this.updateForm(installation);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const installation = this.createFromForm();
    if (installation.id !== undefined) {
      this.subscribeToSaveResponse(this.installationService.update(installation));
    } else {
      this.subscribeToSaveResponse(this.installationService.create(installation));
    }
  }

  trackParcelleById(index: number, item: IParcelle): number {
    return item.id!;
  }

  trackBoitierById(index: number, item: IBoitier): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IInstallation>>): void {
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

  protected updateForm(installation: IInstallation): void {
    this.editForm.patchValue({
      id: installation.id,
      dateDebut: installation.dateDebut ? installation.dateDebut.format(DATE_TIME_FORMAT) : null,
      dateFin: installation.dateFin ? installation.dateFin.format(DATE_TIME_FORMAT) : null,
      parcelle: installation.parcelle,
      boitier: installation.boitier,
    });

    this.parcellesSharedCollection = this.parcelleService.addParcelleToCollectionIfMissing(
      this.parcellesSharedCollection,
      installation.parcelle
    );
    this.boitiersSharedCollection = this.boitierService.addBoitierToCollectionIfMissing(
      this.boitiersSharedCollection,
      installation.boitier
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

    this.boitierService
      .query()
      .pipe(map((res: HttpResponse<IBoitier[]>) => res.body ?? []))
      .pipe(
        map((boitiers: IBoitier[]) => this.boitierService.addBoitierToCollectionIfMissing(boitiers, this.editForm.get('boitier')!.value))
      )
      .subscribe((boitiers: IBoitier[]) => (this.boitiersSharedCollection = boitiers));
  }

  protected createFromForm(): IInstallation {
    return {
      ...new Installation(),
      id: this.editForm.get(['id'])!.value,
      dateDebut: this.editForm.get(['dateDebut'])!.value ? dayjs(this.editForm.get(['dateDebut'])!.value, DATE_TIME_FORMAT) : undefined,
      dateFin: this.editForm.get(['dateFin'])!.value ? dayjs(this.editForm.get(['dateFin'])!.value, DATE_TIME_FORMAT) : undefined,
      parcelle: this.editForm.get(['parcelle'])!.value,
      boitier: this.editForm.get(['boitier'])!.value,
    };
  }
}
