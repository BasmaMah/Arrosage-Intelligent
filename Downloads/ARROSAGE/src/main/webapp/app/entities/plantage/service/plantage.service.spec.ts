import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import dayjs from 'dayjs/esm';

import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IPlantage, Plantage } from '../plantage.model';

import { PlantageService } from './plantage.service';

describe('Plantage Service', () => {
  let service: PlantageService;
  let httpMock: HttpTestingController;
  let elemDefault: IPlantage;
  let expectedResult: IPlantage | IPlantage[] | boolean | null;
  let currentDate: dayjs.Dayjs;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(PlantageService);
    httpMock = TestBed.inject(HttpTestingController);
    currentDate = dayjs();

    elemDefault = {
      id: 0,
      date: currentDate,
      nombre: 0,
    };
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = Object.assign(
        {
          date: currentDate.format(DATE_TIME_FORMAT),
        },
        elemDefault
      );

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(elemDefault);
    });

    it('should create a Plantage', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
          date: currentDate.format(DATE_TIME_FORMAT),
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          date: currentDate,
        },
        returnedFromService
      );

      service.create(new Plantage()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Plantage', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          date: currentDate.format(DATE_TIME_FORMAT),
          nombre: 1,
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          date: currentDate,
        },
        returnedFromService
      );

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Plantage', () => {
      const patchObject = Object.assign(
        {
          date: currentDate.format(DATE_TIME_FORMAT),
          nombre: 1,
        },
        new Plantage()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign(
        {
          date: currentDate,
        },
        returnedFromService
      );

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Plantage', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          date: currentDate.format(DATE_TIME_FORMAT),
          nombre: 1,
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          date: currentDate,
        },
        returnedFromService
      );

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toContainEqual(expected);
    });

    it('should delete a Plantage', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addPlantageToCollectionIfMissing', () => {
      it('should add a Plantage to an empty array', () => {
        const plantage: IPlantage = { id: 123 };
        expectedResult = service.addPlantageToCollectionIfMissing([], plantage);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(plantage);
      });

      it('should not add a Plantage to an array that contains it', () => {
        const plantage: IPlantage = { id: 123 };
        const plantageCollection: IPlantage[] = [
          {
            ...plantage,
          },
          { id: 456 },
        ];
        expectedResult = service.addPlantageToCollectionIfMissing(plantageCollection, plantage);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Plantage to an array that doesn't contain it", () => {
        const plantage: IPlantage = { id: 123 };
        const plantageCollection: IPlantage[] = [{ id: 456 }];
        expectedResult = service.addPlantageToCollectionIfMissing(plantageCollection, plantage);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(plantage);
      });

      it('should add only unique Plantage to an array', () => {
        const plantageArray: IPlantage[] = [{ id: 123 }, { id: 456 }, { id: 65350 }];
        const plantageCollection: IPlantage[] = [{ id: 123 }];
        expectedResult = service.addPlantageToCollectionIfMissing(plantageCollection, ...plantageArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const plantage: IPlantage = { id: 123 };
        const plantage2: IPlantage = { id: 456 };
        expectedResult = service.addPlantageToCollectionIfMissing([], plantage, plantage2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(plantage);
        expect(expectedResult).toContain(plantage2);
      });

      it('should accept null and undefined values', () => {
        const plantage: IPlantage = { id: 123 };
        expectedResult = service.addPlantageToCollectionIfMissing([], null, plantage, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(plantage);
      });

      it('should return initial array if no Plantage is added', () => {
        const plantageCollection: IPlantage[] = [{ id: 123 }];
        expectedResult = service.addPlantageToCollectionIfMissing(plantageCollection, undefined, null);
        expect(expectedResult).toEqual(plantageCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
