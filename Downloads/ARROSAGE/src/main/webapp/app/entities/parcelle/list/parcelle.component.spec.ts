import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';

import { ParcelleService } from '../service/parcelle.service';

import { ParcelleComponent } from './parcelle.component';

describe('Parcelle Management Component', () => {
  let comp: ParcelleComponent;
  let fixture: ComponentFixture<ParcelleComponent>;
  let service: ParcelleService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      declarations: [ParcelleComponent],
    })
      .overrideTemplate(ParcelleComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ParcelleComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(ParcelleService);

    const headers = new HttpHeaders();
    jest.spyOn(service, 'query').mockReturnValue(
      of(
        new HttpResponse({
          body: [{ id: 123 }],
          headers,
        })
      )
    );
  });

  it('Should call load all on init', () => {
    // WHEN
    comp.ngOnInit();

    // THEN
    expect(service.query).toHaveBeenCalled();
    expect(comp.parcelles?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });
});
