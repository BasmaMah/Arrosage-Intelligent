import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IConnecte, Connecte } from '../connecte.model';
import { ConnecteService } from '../service/connecte.service';
import { IBoitier } from 'app/entities/boitier/boitier.model';
import { BoitierService } from 'app/entities/boitier/service/boitier.service';
import { ICapteur } from 'app/entities/capteur/capteur.model';
import { CapteurService } from 'app/entities/capteur/service/capteur.service';

@Component({
  selector: 'jhi-connecte-update',
  templateUrl: './connecte-update.component.html',
})
export class ConnecteUpdateComponent implements OnInit {
  isSaving = false;

  boitiersSharedCollection: IBoitier[] = [];
  capteursSharedCollection: ICapteur[] = [];

  editForm = this.fb.group({
    id: [],
    fonctionnel: [],
    branche: [],
    frequence: [],
    marge: [],
    boitier: [],
    capteur: [],
  });

  constructor(
    protected connecteService: ConnecteService,
    protected boitierService: BoitierService,
    protected capteurService: CapteurService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ connecte }) => {
      this.updateForm(connecte);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const connecte = this.createFromForm();
    if (connecte.id !== undefined) {
      this.subscribeToSaveResponse(this.connecteService.update(connecte));
    } else {
      this.subscribeToSaveResponse(this.connecteService.create(connecte));
    }
  }

  trackBoitierById(index: number, item: IBoitier): number {
    return item.id!;
  }

  trackCapteurById(index: number, item: ICapteur): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IConnecte>>): void {
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

  protected updateForm(connecte: IConnecte): void {
    this.editForm.patchValue({
      id: connecte.id,
      fonctionnel: connecte.fonctionnel,
      branche: connecte.branche,
      frequence: connecte.frequence,
      marge: connecte.marge,
      boitier: connecte.boitier,
      capteur: connecte.capteur,
    });

    this.boitiersSharedCollection = this.boitierService.addBoitierToCollectionIfMissing(this.boitiersSharedCollection, connecte.boitier);
    this.capteursSharedCollection = this.capteurService.addCapteurToCollectionIfMissing(this.capteursSharedCollection, connecte.capteur);
  }

  protected loadRelationshipsOptions(): void {
    this.boitierService
      .query()
      .pipe(map((res: HttpResponse<IBoitier[]>) => res.body ?? []))
      .pipe(
        map((boitiers: IBoitier[]) => this.boitierService.addBoitierToCollectionIfMissing(boitiers, this.editForm.get('boitier')!.value))
      )
      .subscribe((boitiers: IBoitier[]) => (this.boitiersSharedCollection = boitiers));

    this.capteurService
      .query()
      .pipe(map((res: HttpResponse<ICapteur[]>) => res.body ?? []))
      .pipe(
        map((capteurs: ICapteur[]) => this.capteurService.addCapteurToCollectionIfMissing(capteurs, this.editForm.get('capteur')!.value))
      )
      .subscribe((capteurs: ICapteur[]) => (this.capteursSharedCollection = capteurs));
  }

  protected createFromForm(): IConnecte {
    return {
      ...new Connecte(),
      id: this.editForm.get(['id'])!.value,
      fonctionnel: this.editForm.get(['fonctionnel'])!.value,
      branche: this.editForm.get(['branche'])!.value,
      frequence: this.editForm.get(['frequence'])!.value,
      marge: this.editForm.get(['marge'])!.value,
      boitier: this.editForm.get(['boitier'])!.value,
      capteur: this.editForm.get(['capteur'])!.value,
    };
  }
}
