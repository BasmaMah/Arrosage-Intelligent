import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'extra-user',
        data: { pageTitle: 'ExtraUsers' },
        loadChildren: () => import('./extra-user/extra-user.module').then(m => m.ExtraUserModule),
      },
      {
        path: 'arrosage',
        data: { pageTitle: 'Arrosages' },
        loadChildren: () => import('./arrosage/arrosage.module').then(m => m.ArrosageModule),
      },
      {
        path: 'ferme',
        data: { pageTitle: 'Fermes' },
        loadChildren: () => import('./ferme/ferme.module').then(m => m.FermeModule),
      },
      {
        path: 'parcelle',
        data: { pageTitle: 'Parcelles' },
        loadChildren: () => import('./parcelle/parcelle.module').then(m => m.ParcelleModule),
      },
      {
        path: 'plante',
        data: { pageTitle: 'Plantes' },
        loadChildren: () => import('./plante/plante.module').then(m => m.PlanteModule),
      },
      {
        path: 'type-plante',
        data: { pageTitle: 'TypePlantes' },
        loadChildren: () => import('./type-plante/type-plante.module').then(m => m.TypePlanteModule),
      },
      {
        path: 'grandeur',
        data: { pageTitle: 'Grandeurs' },
        loadChildren: () => import('./grandeur/grandeur.module').then(m => m.GrandeurModule),
      },
      {
        path: 'type-sol',
        data: { pageTitle: 'TypeSols' },
        loadChildren: () => import('./type-sol/type-sol.module').then(m => m.TypeSolModule),
      },
      {
        path: 'boitier',
        data: { pageTitle: 'Boitiers' },
        loadChildren: () => import('./boitier/boitier.module').then(m => m.BoitierModule),
      },
      {
        path: 'installation',
        data: { pageTitle: 'Installations' },
        loadChildren: () => import('./installation/installation.module').then(m => m.InstallationModule),
      },
      {
        path: 'capteur',
        data: { pageTitle: 'Capteurs' },
        loadChildren: () => import('./capteur/capteur.module').then(m => m.CapteurModule),
      },
      {
        path: 'plantage',
        data: { pageTitle: 'Plantages' },
        loadChildren: () => import('./plantage/plantage.module').then(m => m.PlantageModule),
      },
      {
        path: 'connecte',
        data: { pageTitle: 'Connectes' },
        loadChildren: () => import('./connecte/connecte.module').then(m => m.ConnecteModule),
      },
      {
        path: 'notification',
        data: { pageTitle: 'Notifications' },
        loadChildren: () => import('./notification/notification.module').then(m => m.NotificationModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
