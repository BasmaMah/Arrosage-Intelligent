import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { ArrosageService } from '../service/arrosage.service';
import { IArrosage, Arrosage } from '../arrosage.model';
import { IParcelle } from 'app/entities/parcelle/parcelle.model';
import { ParcelleService } from 'app/entities/parcelle/service/parcelle.service';

import { ArrosageUpdateComponent } from './arrosage-update.component';

describe('Arrosage Management Update Component', () => {
  let comp: ArrosageUpdateComponent;
  let fixture: ComponentFixture<ArrosageUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let arrosageService: ArrosageService;
  let parcelleService: ParcelleService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [ArrosageUpdateComponent],
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
      .overrideTemplate(ArrosageUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ArrosageUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    arrosageService = TestBed.inject(ArrosageService);
    parcelleService = TestBed.inject(ParcelleService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Parcelle query and add missing value', () => {
      const arrosage: IArrosage = { id: 456 };
      const parcelle: IParcelle = { id: 8390 };
      arrosage.parcelle = parcelle;

      const parcelleCollection: IParcelle[] = [{ id: 69393 }];
      jest.spyOn(parcelleService, 'query').mockReturnValue(of(new HttpResponse({ body: parcelleCollection })));
      const additionalParcelles = [parcelle];
      const expectedCollection: IParcelle[] = [...additionalParcelles, ...parcelleCollection];
      jest.spyOn(parcelleService, 'addParcelleToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ arrosage });
      comp.ngOnInit();

      expect(parcelleService.query).toHaveBeenCalled();
      expect(parcelleService.addParcelleToCollectionIfMissing).toHaveBeenCalledWith(parcelleCollection, ...additionalParcelles);
      expect(comp.parcellesSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const arrosage: IArrosage = { id: 456 };
      const parcelle: IParcelle = { id: 43096 };
      arrosage.parcelle = parcelle;

      activatedRoute.data = of({ arrosage });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(arrosage));
      expect(comp.parcellesSharedCollection).toContain(parcelle);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Arrosage>>();
      const arrosage = { id: 123 };
      jest.spyOn(arrosageService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ arrosage });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: arrosage }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(arrosageService.update).toHaveBeenCalledWith(arrosage);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Arrosage>>();
      const arrosage = new Arrosage();
      jest.spyOn(arrosageService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ arrosage });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: arrosage }));
      saveSubject.complete();

      // THEN
      expect(arrosageService.create).toHaveBeenCalledWith(arrosage);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Arrosage>>();
      const arrosage = { id: 123 };
      jest.spyOn(arrosageService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ arrosage });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(arrosageService.update).toHaveBeenCalledWith(arrosage);
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
  });
});
