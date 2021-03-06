import { Component, OnInit, ElementRef } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { ICapteur, Capteur } from '../capteur.model';
import { CapteurService } from '../service/capteur.service';
import { AlertError } from 'app/shared/alert/alert-error.model';
import { EventManager, EventWithContent } from 'app/core/util/event-manager.service';
import { DataUtils, FileLoadError } from 'app/core/util/data-util.service';
import { IBoitier } from 'app/entities/boitier/boitier.model';
import { BoitierService } from 'app/entities/boitier/service/boitier.service';

@Component({
  selector: 'jhi-capteur-update',
  templateUrl: './capteur-update.component.html',
})
export class CapteurUpdateComponent implements OnInit {
  isSaving = false;

  boitiersSharedCollection: IBoitier[] = [];

  editForm = this.fb.group({
    id: [],
    ref: [],
    type: [],
    photo: [],
    photoContentType: [],
    boitier: [],
  });

  constructor(
    protected dataUtils: DataUtils,
    protected eventManager: EventManager,
    protected capteurService: CapteurService,
    protected boitierService: BoitierService,
    protected elementRef: ElementRef,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ capteur }) => {
      this.updateForm(capteur);

      this.loadRelationshipsOptions();
    });
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    this.dataUtils.openFile(base64String, contentType);
  }

  setFileData(event: Event, field: string, isImage: boolean): void {
    this.dataUtils.loadFileToForm(event, this.editForm, field, isImage).subscribe({
      error: (err: FileLoadError) =>
        this.eventManager.broadcast(new EventWithContent<AlertError>('arrosageApp.error', { message: err.message })),
    });
  }

  clearInputImage(field: string, fieldContentType: string, idInput: string): void {
    this.editForm.patchValue({
      [field]: null,
      [fieldContentType]: null,
    });
    if (idInput && this.elementRef.nativeElement.querySelector('#' + idInput)) {
      this.elementRef.nativeElement.querySelector('#' + idInput).value = null;
    }
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const capteur = this.createFromForm();
    if (capteur.id !== undefined) {
      this.subscribeToSaveResponse(this.capteurService.update(capteur));
    } else {
      this.subscribeToSaveResponse(this.capteurService.create(capteur));
    }
  }

  trackBoitierById(index: number, item: IBoitier): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ICapteur>>): void {
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

  protected updateForm(capteur: ICapteur): void {
    this.editForm.patchValue({
      id: capteur.id,
      ref: capteur.ref,
      type: capteur.type,
      photo: capteur.photo,
      photoContentType: capteur.photoContentType,
      boitier: capteur.boitier,
    });

    this.boitiersSharedCollection = this.boitierService.addBoitierToCollectionIfMissing(this.boitiersSharedCollection, capteur.boitier);
  }

  protected loadRelationshipsOptions(): void {
    this.boitierService
      .query()
      .pipe(map((res: HttpResponse<IBoitier[]>) => res.body ?? []))
      .pipe(
        map((boitiers: IBoitier[]) => this.boitierService.addBoitierToCollectionIfMissing(boitiers, this.editForm.get('boitier')!.value))
      )
      .subscribe((boitiers: IBoitier[]) => (this.boitiersSharedCollection = boitiers));
  }

  protected createFromForm(): ICapteur {
    return {
      ...new Capteur(),
      id: this.editForm.get(['id'])!.value,
      ref: this.editForm.get(['ref'])!.value,
      type: this.editForm.get(['type'])!.value,
      photoContentType: this.editForm.get(['photoContentType'])!.value,
      photo: this.editForm.get(['photo'])!.value,
      boitier: this.editForm.get(['boitier'])!.value,
    };
  }
}
