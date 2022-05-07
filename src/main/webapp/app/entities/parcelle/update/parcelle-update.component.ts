import { Component, OnInit, ElementRef } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IParcelle, Parcelle } from '../parcelle.model';
import { ParcelleService } from '../service/parcelle.service';
import { AlertError } from 'app/shared/alert/alert-error.model';
import { EventManager, EventWithContent } from 'app/core/util/event-manager.service';
import { DataUtils, FileLoadError } from 'app/core/util/data-util.service';
import { ITypeSol } from 'app/entities/type-sol/type-sol.model';
import { TypeSolService } from 'app/entities/type-sol/service/type-sol.service';
import { IFerme } from 'app/entities/ferme/ferme.model';
import { FermeService } from 'app/entities/ferme/service/ferme.service';

@Component({
  selector: 'jhi-parcelle-update',
  templateUrl: './parcelle-update.component.html',
})
export class ParcelleUpdateComponent implements OnInit {
  isSaving = false;

  typeSolsSharedCollection: ITypeSol[] = [];
  fermesSharedCollection: IFerme[] = [];

  editForm = this.fb.group({
    id: [],
    surface: [],
    photo: [],
    photoContentType: [],
    libelle: [],
    typeSol: [],
    ferme: [],
  });

  constructor(
    protected dataUtils: DataUtils,
    protected eventManager: EventManager,
    protected parcelleService: ParcelleService,
    protected typeSolService: TypeSolService,
    protected fermeService: FermeService,
    protected elementRef: ElementRef,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ parcelle }) => {
      this.updateForm(parcelle);

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
    const parcelle = this.createFromForm();
    if (parcelle.id !== undefined) {
      this.subscribeToSaveResponse(this.parcelleService.update(parcelle));
    } else {
      this.subscribeToSaveResponse(this.parcelleService.create(parcelle));
    }
  }

  trackTypeSolById(index: number, item: ITypeSol): number {
    return item.id!;
  }

  trackFermeById(index: number, item: IFerme): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IParcelle>>): void {
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

  protected updateForm(parcelle: IParcelle): void {
    this.editForm.patchValue({
      id: parcelle.id,
      surface: parcelle.surface,
      photo: parcelle.photo,
      photoContentType: parcelle.photoContentType,
      libelle: parcelle.libelle,
      typeSol: parcelle.typeSol,
      ferme: parcelle.ferme,
    });

    this.typeSolsSharedCollection = this.typeSolService.addTypeSolToCollectionIfMissing(this.typeSolsSharedCollection, parcelle.typeSol);
    this.fermesSharedCollection = this.fermeService.addFermeToCollectionIfMissing(this.fermesSharedCollection, parcelle.ferme);
  }

  protected loadRelationshipsOptions(): void {
    this.typeSolService
      .query()
      .pipe(map((res: HttpResponse<ITypeSol[]>) => res.body ?? []))
      .pipe(
        map((typeSols: ITypeSol[]) => this.typeSolService.addTypeSolToCollectionIfMissing(typeSols, this.editForm.get('typeSol')!.value))
      )
      .subscribe((typeSols: ITypeSol[]) => (this.typeSolsSharedCollection = typeSols));

    this.fermeService
      .query()
      .pipe(map((res: HttpResponse<IFerme[]>) => res.body ?? []))
      .pipe(map((fermes: IFerme[]) => this.fermeService.addFermeToCollectionIfMissing(fermes, this.editForm.get('ferme')!.value)))
      .subscribe((fermes: IFerme[]) => (this.fermesSharedCollection = fermes));
  }

  protected createFromForm(): IParcelle {
    return {
      ...new Parcelle(),
      id: this.editForm.get(['id'])!.value,
      surface: this.editForm.get(['surface'])!.value,
      photoContentType: this.editForm.get(['photoContentType'])!.value,
      photo: this.editForm.get(['photo'])!.value,
      libelle: this.editForm.get(['libelle'])!.value,
      typeSol: this.editForm.get(['typeSol'])!.value,
      ferme: this.editForm.get(['ferme'])!.value,
    };
  }
}
