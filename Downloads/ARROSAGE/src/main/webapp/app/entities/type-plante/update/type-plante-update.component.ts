import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { ITypePlante, TypePlante } from '../type-plante.model';
import { TypePlanteService } from '../service/type-plante.service';

@Component({
  selector: 'jhi-type-plante-update',
  templateUrl: './type-plante-update.component.html',
})
export class TypePlanteUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    libelle: [null, [Validators.required]],
    humiditeMax: [],
    humiditeMin: [],
    temperature: [],
    luminosite: [],
  });

  constructor(protected typePlanteService: TypePlanteService, protected activatedRoute: ActivatedRoute, protected fb: FormBuilder) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ typePlante }) => {
      this.updateForm(typePlante);
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const typePlante = this.createFromForm();
    if (typePlante.id !== undefined) {
      this.subscribeToSaveResponse(this.typePlanteService.update(typePlante));
    } else {
      this.subscribeToSaveResponse(this.typePlanteService.create(typePlante));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ITypePlante>>): void {
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

  protected updateForm(typePlante: ITypePlante): void {
    this.editForm.patchValue({
      id: typePlante.id,
      libelle: typePlante.libelle,
      humiditeMax: typePlante.humiditeMax,
      humiditeMin: typePlante.humiditeMin,
      temperature: typePlante.temperature,
      luminosite: typePlante.luminosite,
    });
  }

  protected createFromForm(): ITypePlante {
    return {
      ...new TypePlante(),
      id: this.editForm.get(['id'])!.value,
      libelle: this.editForm.get(['libelle'])!.value,
      humiditeMax: this.editForm.get(['humiditeMax'])!.value,
      humiditeMin: this.editForm.get(['humiditeMin'])!.value,
      temperature: this.editForm.get(['temperature'])!.value,
      luminosite: this.editForm.get(['luminosite'])!.value,
    };
  }
}
