import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { InstallationService } from '../service/installation.service';
import { IInstallation, Installation } from '../installation.model';
import { IParcelle } from 'app/entities/parcelle/parcelle.model';
import { ParcelleService } from 'app/entities/parcelle/service/parcelle.service';
import { IBoitier } from 'app/entities/boitier/boitier.model';
import { BoitierService } from 'app/entities/boitier/service/boitier.service';

import { InstallationUpdateComponent } from './installation-update.component';

describe('Installation Management Update Component', () => {
  let comp: InstallationUpdateComponent;
  let fixture: ComponentFixture<InstallationUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let installationService: InstallationService;
  let parcelleService: ParcelleService;
  let boitierService: BoitierService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [InstallationUpdateComponent],
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
      .overrideTemplate(InstallationUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(InstallationUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    installationService = TestBed.inject(InstallationService);
    parcelleService = TestBed.inject(ParcelleService);
    boitierService = TestBed.inject(BoitierService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Parcelle query and add missing value', () => {
      const installation: IInstallation = { id: 456 };
      const parcelle: IParcelle = { id: 80384 };
      installation.parcelle = parcelle;

      const parcelleCollection: IParcelle[] = [{ id: 20798 }];
      jest.spyOn(parcelleService, 'query').mockReturnValue(of(new HttpResponse({ body: parcelleCollection })));
      const additionalParcelles = [parcelle];
      const expectedCollection: IParcelle[] = [...additionalParcelles, ...parcelleCollection];
      jest.spyOn(parcelleService, 'addParcelleToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ installation });
      comp.ngOnInit();

      expect(parcelleService.query).toHaveBeenCalled();
      expect(parcelleService.addParcelleToCollectionIfMissing).toHaveBeenCalledWith(parcelleCollection, ...additionalParcelles);
      expect(comp.parcellesSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Boitier query and add missing value', () => {
      const installation: IInstallation = { id: 456 };
      const boitier: IBoitier = { id: 14991 };
      installation.boitier = boitier;

      const boitierCollection: IBoitier[] = [{ id: 13339 }];
      jest.spyOn(boitierService, 'query').mockReturnValue(of(new HttpResponse({ body: boitierCollection })));
      const additionalBoitiers = [boitier];
      const expectedCollection: IBoitier[] = [...additionalBoitiers, ...boitierCollection];
      jest.spyOn(boitierService, 'addBoitierToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ installation });
      comp.ngOnInit();

      expect(boitierService.query).toHaveBeenCalled();
      expect(boitierService.addBoitierToCollectionIfMissing).toHaveBeenCalledWith(boitierCollection, ...additionalBoitiers);
      expect(comp.boitiersSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const installation: IInstallation = { id: 456 };
      const parcelle: IParcelle = { id: 71503 };
      installation.parcelle = parcelle;
      const boitier: IBoitier = { id: 8022 };
      installation.boitier = boitier;

      activatedRoute.data = of({ installation });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(installation));
      expect(comp.parcellesSharedCollection).toContain(parcelle);
      expect(comp.boitiersSharedCollection).toContain(boitier);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Installation>>();
      const installation = { id: 123 };
      jest.spyOn(installationService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ installation });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: installation }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(installationService.update).toHaveBeenCalledWith(installation);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Installation>>();
      const installation = new Installation();
      jest.spyOn(installationService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ installation });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: installation }));
      saveSubject.complete();

      // THEN
      expect(installationService.create).toHaveBeenCalledWith(installation);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Installation>>();
      const installation = { id: 123 };
      jest.spyOn(installationService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ installation });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(installationService.update).toHaveBeenCalledWith(installation);
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

    describe('trackBoitierById', () => {
      it('Should return tracked Boitier primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackBoitierById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });
  });
});
