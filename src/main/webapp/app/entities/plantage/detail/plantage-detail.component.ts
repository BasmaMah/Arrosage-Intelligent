import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IPlantage } from '../plantage.model';

@Component({
  selector: 'jhi-plantage-detail',
  templateUrl: './plantage-detail.component.html',
})
export class PlantageDetailComponent implements OnInit {
  plantage: IPlantage | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ plantage }) => {
      this.plantage = plantage;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
