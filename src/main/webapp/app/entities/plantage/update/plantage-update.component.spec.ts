import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { PlantageService } from '../service/plantage.service';
import { IPlantage, Plantage } from '../plantage.model';
import { IParcelle } from 'app/entities/parcelle/parcelle.model';
import { ParcelleService } from 'app/entities/parcelle/service/parcelle.service';
import { IPlante } from 'app/entities/plante/plante.model';
import { PlanteService } from 'app/entities/plante/service/plante.service';

import { PlantageUpdateComponent } from './plantage-update.component';

describe('Plantage Management Update Component', () => {
  let comp: PlantageUpdateComponent;
  let fixture: ComponentFixture<PlantageUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let plantageService: PlantageService;
  let parcelleService: ParcelleService;
  let planteService: PlanteService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [PlantageUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(PlantageUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(PlantageUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    plantageService = TestBed.inject(PlantageService);
    parcelleService = TestBed.inject(ParcelleService);
    planteService = TestBed.inject(PlanteService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Parcelle query and add missing value', () => {
      const plantage: IPlantage = { id: 456 };
      const parcelle: IParcelle = { id: 56702 };
      plantage.parcelle = parcelle;

      const parcelleCollection: IParcelle[] = [{ id: 36638 }];
      jest.spyOn(parcelleService, 'query').mockReturnValue(of(new HttpResponse({ body: parcelleCollection })));
      const additionalParcelles = [parcelle];
      const expectedCollection: IParcelle[] = [...additionalParcelles, ...parcelleCollection];
      jest.spyOn(parcelleService, 'addParcelleToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ plantage });
      comp.ngOnInit();

      expect(parcelleService.query).toHaveBeenCalled();
      expect(parcelleService.addParcelleToCollectionIfMissing).toHaveBeenCalledWith(parcelleCollection, ...additionalParcelles);
      expect(comp.parcellesSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Plante query and add missing value', () => {
      const plantage: IPlantage = { id: 456 };
      const plante: IPlante = { id: 64206 };
      plantage.plante = plante;

      const planteCollection: IPlante[] = [{ id: 16243 }];
      jest.spyOn(planteService, 'query').mockReturnValue(of(new HttpResponse({ body: planteCollection })));
      const additionalPlantes = [plante];
      const expectedCollection: IPlante[] = [...additionalPlantes, ...planteCollection];
      jest.spyOn(planteService, 'addPlanteToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ plantage });
      comp.ngOnInit();

      expect(planteService.query).toHaveBeenCalled();
      expect(planteService.addPlanteToCollectionIfMissing).toHaveBeenCalledWith(planteCollection, ...additionalPlantes);
      expect(comp.plantesSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const plantage: IPlantage = { id: 456 };
      const parcelle: IParcelle = { id: 2477 };
      plantage.parcelle = parcelle;
      const plante: IPlante = { id: 48333 };
      plantage.plante = plante;

      activatedRoute.data = of({ plantage });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(plantage));
      expect(comp.parcellesSharedCollection).toContain(parcelle);
      expect(comp.plantesSharedCollection).toContain(plante);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Plantage>>();
      const plantage = { id: 123 };
      jest.spyOn(plantageService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ plantage });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: plantage }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(plantageService.update).toHaveBeenCalledWith(plantage);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Plantage>>();
      const plantage = new Plantage();
      jest.spyOn(plantageService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ plantage });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: plantage }));
      saveSubject.complete();

      // THEN
      expect(plantageService.create).toHaveBeenCalledWith(plantage);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Plantage>>();
      const plantage = { id: 123 };
      jest.spyOn(plantageService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ plantage });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(plantageService.update).toHaveBeenCalledWith(plantage);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Tracking relationships identifiers', () => {
    describe('trackParcelleById', () => {
      it('Should return tracked Parcelle primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackParcelleById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });

    describe('trackPlanteById', () => {
      it('Should return tracked Plante primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackPlanteById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });
  });
});
