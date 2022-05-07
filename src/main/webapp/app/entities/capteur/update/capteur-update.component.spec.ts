import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { CapteurService } from '../service/capteur.service';
import { ICapteur, Capteur } from '../capteur.model';
import { IBoitier } from 'app/entities/boitier/boitier.model';
import { BoitierService } from 'app/entities/boitier/service/boitier.service';

import { CapteurUpdateComponent } from './capteur-update.component';

describe('Capteur Management Update Component', () => {
  let comp: CapteurUpdateComponent;
  let fixture: ComponentFixture<CapteurUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let capteurService: CapteurService;
  let boitierService: BoitierService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [CapteurUpdateComponent],
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
      .overrideTemplate(CapteurUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(CapteurUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    capteurService = TestBed.inject(CapteurService);
    boitierService = TestBed.inject(BoitierService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Boitier query and add missing value', () => {
      const capteur: ICapteur = { id: 456 };
      const boitier: IBoitier = { id: 89650 };
      capteur.boitier = boitier;

      const boitierCollection: IBoitier[] = [{ id: 37104 }];
      jest.spyOn(boitierService, 'query').mockReturnValue(of(new HttpResponse({ body: boitierCollection })));
      const additionalBoitiers = [boitier];
      const expectedCollection: IBoitier[] = [...additionalBoitiers, ...boitierCollection];
      jest.spyOn(boitierService, 'addBoitierToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ capteur });
      comp.ngOnInit();

      expect(boitierService.query).toHaveBeenCalled();
      expect(boitierService.addBoitierToCollectionIfMissing).toHaveBeenCalledWith(boitierCollection, ...additionalBoitiers);
      expect(comp.boitiersSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const capteur: ICapteur = { id: 456 };
      const boitier: IBoitier = { id: 61018 };
      capteur.boitier = boitier;

      activatedRoute.data = of({ capteur });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(capteur));
      expect(comp.boitiersSharedCollection).toContain(boitier);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Capteur>>();
      const capteur = { id: 123 };
      jest.spyOn(capteurService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ capteur });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: capteur }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(capteurService.update).toHaveBeenCalledWith(capteur);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Capteur>>();
      const capteur = new Capteur();
      jest.spyOn(capteurService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ capteur });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: capteur }));
      saveSubject.complete();

      // THEN
      expect(capteurService.create).toHaveBeenCalledWith(capteur);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Capteur>>();
      const capteur = { id: 123 };
      jest.spyOn(capteurService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ capteur });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(capteurService.update).toHaveBeenCalledWith(capteur);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Tracking relationships identifiers', () => {
    describe('trackBoitierById', () => {
      it('Should return tracked Boitier primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackBoitierById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });
  });
});
