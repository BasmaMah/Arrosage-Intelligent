import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { ParcelleService } from '../service/parcelle.service';
import { IParcelle, Parcelle } from '../parcelle.model';
import { ITypeSol } from 'app/entities/type-sol/type-sol.model';
import { TypeSolService } from 'app/entities/type-sol/service/type-sol.service';
import { IFerme } from 'app/entities/ferme/ferme.model';
import { FermeService } from 'app/entities/ferme/service/ferme.service';

import { ParcelleUpdateComponent } from './parcelle-update.component';

describe('Parcelle Management Update Component', () => {
  let comp: ParcelleUpdateComponent;
  let fixture: ComponentFixture<ParcelleUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let parcelleService: ParcelleService;
  let typeSolService: TypeSolService;
  let fermeService: FermeService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [ParcelleUpdateComponent],
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
      .overrideTemplate(ParcelleUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ParcelleUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    parcelleService = TestBed.inject(ParcelleService);
    typeSolService = TestBed.inject(TypeSolService);
    fermeService = TestBed.inject(FermeService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call TypeSol query and add missing value', () => {
      const parcelle: IParcelle = { id: 456 };
      const typeSol: ITypeSol = { id: 32644 };
      parcelle.typeSol = typeSol;

      const typeSolCollection: ITypeSol[] = [{ id: 38188 }];
      jest.spyOn(typeSolService, 'query').mockReturnValue(of(new HttpResponse({ body: typeSolCollection })));
      const additionalTypeSols = [typeSol];
      const expectedCollection: ITypeSol[] = [...additionalTypeSols, ...typeSolCollection];
      jest.spyOn(typeSolService, 'addTypeSolToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ parcelle });
      comp.ngOnInit();

      expect(typeSolService.query).toHaveBeenCalled();
      expect(typeSolService.addTypeSolToCollectionIfMissing).toHaveBeenCalledWith(typeSolCollection, ...additionalTypeSols);
      expect(comp.typeSolsSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Ferme query and add missing value', () => {
      const parcelle: IParcelle = { id: 456 };
      const ferme: IFerme = { id: 34866 };
      parcelle.ferme = ferme;

      const fermeCollection: IFerme[] = [{ id: 25499 }];
      jest.spyOn(fermeService, 'query').mockReturnValue(of(new HttpResponse({ body: fermeCollection })));
      const additionalFermes = [ferme];
      const expectedCollection: IFerme[] = [...additionalFermes, ...fermeCollection];
      jest.spyOn(fermeService, 'addFermeToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ parcelle });
      comp.ngOnInit();

      expect(fermeService.query).toHaveBeenCalled();
      expect(fermeService.addFermeToCollectionIfMissing).toHaveBeenCalledWith(fermeCollection, ...additionalFermes);
      expect(comp.fermesSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const parcelle: IParcelle = { id: 456 };
      const typeSol: ITypeSol = { id: 39650 };
      parcelle.typeSol = typeSol;
      const ferme: IFerme = { id: 10994 };
      parcelle.ferme = ferme;

      activatedRoute.data = of({ parcelle });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(parcelle));
      expect(comp.typeSolsSharedCollection).toContain(typeSol);
      expect(comp.fermesSharedCollection).toContain(ferme);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Parcelle>>();
      const parcelle = { id: 123 };
      jest.spyOn(parcelleService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ parcelle });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: parcelle }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(parcelleService.update).toHaveBeenCalledWith(parcelle);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Parcelle>>();
      const parcelle = new Parcelle();
      jest.spyOn(parcelleService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ parcelle });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: parcelle }));
      saveSubject.complete();

      // THEN
      expect(parcelleService.create).toHaveBeenCalledWith(parcelle);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Parcelle>>();
      const parcelle = { id: 123 };
      jest.spyOn(parcelleService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ parcelle });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(parcelleService.update).toHaveBeenCalledWith(parcelle);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Tracking relationships identifiers', () => {
    describe('trackTypeSolById', () => {
      it('Should return tracked TypeSol primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackTypeSolById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });

    describe('trackFermeById', () => {
      it('Should return tracked Ferme primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackFermeById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });
  });
});
