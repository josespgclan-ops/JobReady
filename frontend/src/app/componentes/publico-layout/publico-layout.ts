import { Component, Input } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';

@Component({
  selector: 'app-publico-layout',
  imports: [RouterLink, RouterLinkActive],
  templateUrl: './publico-layout.html',
  styleUrl: './publico-layout.css',
})
export class PublicoLayout {
  @Input({ required: true }) titulo = '';
}
