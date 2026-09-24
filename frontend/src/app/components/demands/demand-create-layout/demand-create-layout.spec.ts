import { ComponentFixture, TestBed } from '@angular/core/testing';
import { DemandCreateLayout } from './demand-create-layout';

describe('DemandCreateLayout', () => {
  let component: DemandCreateLayout;
  let fixture: ComponentFixture<DemandCreateLayout>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DemandCreateLayout],
    }).compileComponents();

    fixture = TestBed.createComponent(DemandCreateLayout);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
