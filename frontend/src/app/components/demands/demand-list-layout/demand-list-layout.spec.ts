import { ComponentFixture, TestBed } from '@angular/core/testing';
import { DemandDefaultLayout } from './demand-list-layout';

describe('DemandDefaultLayout', () => {
  let component: DemandDefaultLayout;
  let fixture: ComponentFixture<DemandDefaultLayout>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DemandDefaultLayout],
    }).compileComponents();

    fixture = TestBed.createComponent(DemandDefaultLayout);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
