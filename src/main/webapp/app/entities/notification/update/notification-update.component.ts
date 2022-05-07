import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';

import { INotification, Notification } from '../notification.model';
import { NotificationService } from '../service/notification.service';
import { IParcelle } from 'app/entities/parcelle/parcelle.model';
import { ParcelleService } from 'app/entities/parcelle/service/parcelle.service';

@Component({
  selector: 'jhi-notification-update',
  templateUrl: './notification-update.component.html',
})
export class NotificationUpdateComponent implements OnInit {
  isSaving = false;

  parcellesSharedCollection: IParcelle[] = [];

  editForm = this.fb.group({
    id: [],
    date: [null, [Validators.required]],
    content: [null, [Validators.required]],
    vu: [null, [Validators.required]],
    parcelle: [],
  });

  constructor(
    protected notificationService: NotificationService,
    protected parcelleService: ParcelleService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ notification }) => {
      if (notification.id === undefined) {
        const today = dayjs().startOf('day');
        notification.date = today;
      }

      this.updateForm(notification);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const notification = this.createFromForm();
    if (notification.id !== undefined) {
      this.subscribeToSaveResponse(this.notificationService.update(notification));
    } else {
      this.subscribeToSaveResponse(this.notificationService.create(notification));
    }
  }

  trackParcelleById(index: number, item: IParcelle): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<INotification>>): void {
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

  protected updateForm(notification: INotification): void {
    this.editForm.patchValue({
      id: notification.id,
      date: notification.date ? notification.date.format(DATE_TIME_FORMAT) : null,
      content: notification.content,
      vu: notification.vu,
      parcelle: notification.parcelle,
    });

    this.parcellesSharedCollection = this.parcelleService.addParcelleToCollectionIfMissing(
      this.parcellesSharedCollection,
      notification.parcelle
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

  protected createFromForm(): INotification {
    return {
      ...new Notification(),
      id: this.editForm.get(['id'])!.value,
      date: this.editForm.get(['date'])!.value ? dayjs(this.editForm.get(['date'])!.value, DATE_TIME_FORMAT) : undefined,
      content: this.editForm.get(['content'])!.value,
      vu: this.editForm.get(['vu'])!.value,
      parcelle: this.editForm.get(['parcelle'])!.value,
    };
  }
}
