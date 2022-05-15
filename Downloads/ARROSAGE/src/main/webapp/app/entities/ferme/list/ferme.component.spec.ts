import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';

import { FermeService } from '../service/ferme.service';

import { FermeComponent } from './ferme.component';

describe('Ferme Management Component', () => {
  let comp: FermeComponent;
  let fixture: ComponentFixture<FermeComponent>;
  let service: FermeService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      declarations: [FermeComponent],
    })
      .overrideTemplate(FermeComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(FermeComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(FermeService);

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
    expect(comp.fermes?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });
});
