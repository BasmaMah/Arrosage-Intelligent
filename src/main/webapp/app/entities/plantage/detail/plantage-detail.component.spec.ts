import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { PlantageDetailComponent } from './plantage-detail.component';

describe('Plantage Management Detail Component', () => {
  let comp: PlantageDetailComponent;
  let fixture: ComponentFixture<PlantageDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [PlantageDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ plantage: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(PlantageDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(PlantageDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load plantage on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.plantage).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
