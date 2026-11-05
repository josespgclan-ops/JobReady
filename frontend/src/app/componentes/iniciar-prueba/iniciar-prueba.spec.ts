import { ComponentFixture, TestBed } from '@angular/core/testing';
import { IniciarPrueba } from './iniciar-prueba';

describe('IniciarPrueba', () => {
  let component: IniciarPrueba;
  let fixture: ComponentFixture<IniciarPrueba>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [IniciarPrueba],
    }).compileComponents();

    fixture = TestBed.createComponent(IniciarPrueba);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
