import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';

import { PlantageService } from '../service/plantage.service';

import { PlantageComponent } from './plantage.component';

describe('Plantage Management Component', () => {
  let comp: PlantageComponent;
  let fixture: ComponentFixture<PlantageComponent>;
  let service: PlantageService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      declarations: [PlantageComponent],
    })
      .overrideTemplate(PlantageComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(PlantageComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(PlantageService);

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
    expect(comp.plantages?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });
});
