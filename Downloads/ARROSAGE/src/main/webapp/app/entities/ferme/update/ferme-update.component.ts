import { Component, OnInit, ElementRef } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IFerme, Ferme } from '../ferme.model';
import { FermeService } from '../service/ferme.service';
import { AlertError } from 'app/shared/alert/alert-error.model';
import { EventManager, EventWithContent } from 'app/core/util/event-manager.service';
import { DataUtils, FileLoadError } from 'app/core/util/data-util.service';
import { IExtraUser } from 'app/entities/extra-user/extra-user.model';
import { ExtraUserService } from 'app/entities/extra-user/service/extra-user.service';

@Component({
  selector: 'jhi-ferme-update',
  templateUrl: './ferme-update.component.html',
})
export class FermeUpdateComponent implements OnInit {
  isSaving = false;

  extraUsersSharedCollection: IExtraUser[] = [];

  editForm = this.fb.group({
    id: [],
    lebelle: [],
    photo: [],
    photoContentType: [],
    extraUser: [],
  });

  constructor(
    protected dataUtils: DataUtils,
    protected eventManager: EventManager,
    protected fermeService: FermeService,
    protected extraUserService: ExtraUserService,
    protected elementRef: ElementRef,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ ferme }) => {
      this.updateForm(ferme);

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
    const ferme = this.createFromForm();
    if (ferme.id !== undefined) {
      this.subscribeToSaveResponse(this.fermeService.update(ferme));
    } else {
      this.subscribeToSaveResponse(this.fermeService.create(ferme));
    }
  }

  trackExtraUserById(index: number, item: IExtraUser): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IFerme>>): void {
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

  protected updateForm(ferme: IFerme): void {
    this.editForm.patchValue({
      id: ferme.id,
      lebelle: ferme.lebelle,
      photo: ferme.photo,
      photoContentType: ferme.photoContentType,
      extraUser: ferme.extraUser,
    });

    this.extraUsersSharedCollection = this.extraUserService.addExtraUserToCollectionIfMissing(
      this.extraUsersSharedCollection,
      ferme.extraUser
    );
  }

  protected loadRelationshipsOptions(): void {
    this.extraUserService
      .query()
      .pipe(map((res: HttpResponse<IExtraUser[]>) => res.body ?? []))
      .pipe(
        map((extraUsers: IExtraUser[]) =>
          this.extraUserService.addExtraUserToCollectionIfMissing(extraUsers, this.editForm.get('extraUser')!.value)
        )
      )
      .subscribe((extraUsers: IExtraUser[]) => (this.extraUsersSharedCollection = extraUsers));
  }

  protected createFromForm(): IFerme {
    return {
      ...new Ferme(),
      id: this.editForm.get(['id'])!.value,
      lebelle: this.editForm.get(['lebelle'])!.value,
      photoContentType: this.editForm.get(['photoContentType'])!.value,
      photo: this.editForm.get(['photo'])!.value,
      extraUser: this.editForm.get(['extraUser'])!.value,
    };
  }
}
