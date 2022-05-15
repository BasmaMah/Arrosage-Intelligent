import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { FermeService } from '../service/ferme.service';
import { IFerme, Ferme } from '../ferme.model';
import { IExtraUser } from 'app/entities/extra-user/extra-user.model';
import { ExtraUserService } from 'app/entities/extra-user/service/extra-user.service';

import { FermeUpdateComponent } from './ferme-update.component';

describe('Ferme Management Update Component', () => {
  let comp: FermeUpdateComponent;
  let fixture: ComponentFixture<FermeUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let fermeService: FermeService;
  let extraUserService: ExtraUserService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [FermeUpdateComponent],
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
      .overrideTemplate(FermeUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(FermeUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    fermeService = TestBed.inject(FermeService);
    extraUserService = TestBed.inject(ExtraUserService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call ExtraUser query and add missing value', () => {
      const ferme: IFerme = { id: 456 };
      const extraUser: IExtraUser = { id: 79359 };
      ferme.extraUser = extraUser;

      const extraUserCollection: IExtraUser[] = [{ id: 73659 }];
      jest.spyOn(extraUserService, 'query').mockReturnValue(of(new HttpResponse({ body: extraUserCollection })));
      const additionalExtraUsers = [extraUser];
      const expectedCollection: IExtraUser[] = [...additionalExtraUsers, ...extraUserCollection];
      jest.spyOn(extraUserService, 'addExtraUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ ferme });
      comp.ngOnInit();

      expect(extraUserService.query).toHaveBeenCalled();
      expect(extraUserService.addExtraUserToCollectionIfMissing).toHaveBeenCalledWith(extraUserCollection, ...additionalExtraUsers);
      expect(comp.extraUsersSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const ferme: IFerme = { id: 456 };
      const extraUser: IExtraUser = { id: 59634 };
      ferme.extraUser = extraUser;

      activatedRoute.data = of({ ferme });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(ferme));
      expect(comp.extraUsersSharedCollection).toContain(extraUser);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Ferme>>();
      const ferme = { id: 123 };
      jest.spyOn(fermeService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ ferme });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: ferme }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(fermeService.update).toHaveBeenCalledWith(ferme);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Ferme>>();
      const ferme = new Ferme();
      jest.spyOn(fermeService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ ferme });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: ferme }));
      saveSubject.complete();

      // THEN
      expect(fermeService.create).toHaveBeenCalledWith(ferme);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Ferme>>();
      const ferme = { id: 123 };
      jest.spyOn(fermeService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ ferme });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(fermeService.update).toHaveBeenCalledWith(ferme);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Tracking relationships identifiers', () => {
    describe('trackExtraUserById', () => {
      it('Should return tracked ExtraUser primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackExtraUserById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });
  });
});
