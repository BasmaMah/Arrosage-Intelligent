import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IFerme, Ferme } from '../ferme.model';

import { FermeService } from './ferme.service';

describe('Ferme Service', () => {
  let service: FermeService;
  let httpMock: HttpTestingController;
  let elemDefault: IFerme;
  let expectedResult: IFerme | IFerme[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(FermeService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      lebelle: 'AAAAAAA',
      photoContentType: 'image/png',
      photo: 'AAAAAAA',
    };
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = Object.assign({}, elemDefault);

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(elemDefault);
    });

    it('should create a Ferme', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Ferme()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Ferme', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          lebelle: 'BBBBBB',
          photo: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Ferme', () => {
      const patchObject = Object.assign(
        {
          lebelle: 'BBBBBB',
          photo: 'BBBBBB',
        },
        new Ferme()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Ferme', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          lebelle: 'BBBBBB',
          photo: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toContainEqual(expected);
    });

    it('should delete a Ferme', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addFermeToCollectionIfMissing', () => {
      it('should add a Ferme to an empty array', () => {
        const ferme: IFerme = { id: 123 };
        expectedResult = service.addFermeToCollectionIfMissing([], ferme);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(ferme);
      });

      it('should not add a Ferme to an array that contains it', () => {
        const ferme: IFerme = { id: 123 };
        const fermeCollection: IFerme[] = [
          {
            ...ferme,
          },
          { id: 456 },
        ];
        expectedResult = service.addFermeToCollectionIfMissing(fermeCollection, ferme);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Ferme to an array that doesn't contain it", () => {
        const ferme: IFerme = { id: 123 };
        const fermeCollection: IFerme[] = [{ id: 456 }];
        expectedResult = service.addFermeToCollectionIfMissing(fermeCollection, ferme);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(ferme);
      });

      it('should add only unique Ferme to an array', () => {
        const fermeArray: IFerme[] = [{ id: 123 }, { id: 456 }, { id: 49368 }];
        const fermeCollection: IFerme[] = [{ id: 123 }];
        expectedResult = service.addFermeToCollectionIfMissing(fermeCollection, ...fermeArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const ferme: IFerme = { id: 123 };
        const ferme2: IFerme = { id: 456 };
        expectedResult = service.addFermeToCollectionIfMissing([], ferme, ferme2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(ferme);
        expect(expectedResult).toContain(ferme2);
      });

      it('should accept null and undefined values', () => {
        const ferme: IFerme = { id: 123 };
        expectedResult = service.addFermeToCollectionIfMissing([], null, ferme, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(ferme);
      });

      it('should return initial array if no Ferme is added', () => {
        const fermeCollection: IFerme[] = [{ id: 123 }];
        expectedResult = service.addFermeToCollectionIfMissing(fermeCollection, undefined, null);
        expect(expectedResult).toEqual(fermeCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
