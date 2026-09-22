import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Demand } from './demand';

describe('Demand', () => {
  let component: Demand;
  let fixture: ComponentFixture<Demand>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Demand],
    }).compileComponents();

    fixture = TestBed.createComponent(Demand);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
