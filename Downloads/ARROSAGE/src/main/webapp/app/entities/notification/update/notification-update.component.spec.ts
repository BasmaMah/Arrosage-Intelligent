import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { NotificationService } from '../service/notification.service';
import { INotification, Notification } from '../notification.model';
import { IParcelle } from 'app/entities/parcelle/parcelle.model';
import { ParcelleService } from 'app/entities/parcelle/service/parcelle.service';

import { NotificationUpdateComponent } from './notification-update.component';

describe('Notification Management Update Component', () => {
  let comp: NotificationUpdateComponent;
  let fixture: ComponentFixture<NotificationUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let notificationService: NotificationService;
  let parcelleService: ParcelleService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [NotificationUpdateComponent],
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
      .overrideTemplate(NotificationUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(NotificationUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    notificationService = TestBed.inject(NotificationService);
    parcelleService = TestBed.inject(ParcelleService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Parcelle query and add missing value', () => {
      const notification: INotification = { id: 456 };
      const parcelle: IParcelle = { id: 868 };
      notification.parcelle = parcelle;

      const parcelleCollection: IParcelle[] = [{ id: 72452 }];
      jest.spyOn(parcelleService, 'query').mockReturnValue(of(new HttpResponse({ body: parcelleCollection })));
      const additionalParcelles = [parcelle];
      const expectedCollection: IParcelle[] = [...additionalParcelles, ...parcelleCollection];
      jest.spyOn(parcelleService, 'addParcelleToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ notification });
      comp.ngOnInit();

      expect(parcelleService.query).toHaveBeenCalled();
      expect(parcelleService.addParcelleToCollectionIfMissing).toHaveBeenCalledWith(parcelleCollection, ...additionalParcelles);
      expect(comp.parcellesSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const notification: INotification = { id: 456 };
      const parcelle: IParcelle = { id: 91526 };
      notification.parcelle = parcelle;

      activatedRoute.data = of({ notification });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(notification));
      expect(comp.parcellesSharedCollection).toContain(parcelle);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Notification>>();
      const notification = { id: 123 };
      jest.spyOn(notificationService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ notification });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: notification }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(notificationService.update).toHaveBeenCalledWith(notification);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Notification>>();
      const notification = new Notification();
      jest.spyOn(notificationService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ notification });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: notification }));
      saveSubject.complete();

      // THEN
      expect(notificationService.create).toHaveBeenCalledWith(notification);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Notification>>();
      const notification = { id: 123 };
      jest.spyOn(notificationService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ notification });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(notificationService.update).toHaveBeenCalledWith(notification);
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
